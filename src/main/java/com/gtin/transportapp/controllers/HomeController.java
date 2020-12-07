/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.controllers;

import com.gtin.transportapp.models.*;
import com.gtin.transportapp.repositories.ClientRepository;
import com.gtin.transportapp.repositories.ParcelRepository;
import com.gtin.transportapp.repositories.TransportRepository;
import com.gtin.transportapp.repositories.UserRepository;
import com.gtin.transportapp.services.MailSender;
import com.gtin.transportapp.services.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    static int transportNumber;

    public static User globalUser;
    public static Client globalClient;
    public static int oneTimeCode;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ParcelRepository parcelRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    TransportRepository transportRepository;

    @Value("${listOfDestinations}")
    private List<String> listOfDestinations;






    /*INDEX*/


    @GetMapping("/")
    public String home(Principal principal) {

        if (principal == null) {
            return "index";
        }

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(RuntimeException::new);
        Client client = clientOptional.get();

        if (client.getRole().equals("Client")) return "client_main";
        if (client.getRole().equals("Driver")) return "driver_main";

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }





    /* USER REGISTRATION*/


    @GetMapping("/register")
    public String registerClient(Model model) {

        Client client = new Client();
        model.addAttribute(client);

        return "register";
    }


    @PostMapping("/register")
    public String submitClient(@ModelAttribute("client") Client client) {


        client.setUserName(client.getEmail());
        //validation process

        Optional<Client> clientOptional = clientRepository.findByUserName(client.getUserName());

        {
            if (clientOptional.isPresent()) return "error_user_already_exists";
            if (!Utilities.isValidEmailAddress(client.getEmail())) return "error_incorrect_email";
            if (!Utilities.isValidPhoneNumber(client.getPhone())) return "error_incorrect_phone_number";
        }


        globalUser = new User();
        globalUser.setPassword(client.getPassword());
        globalUser.setUserName(client.getEmail());
        oneTimeCode = Utilities.generateRegistrationCode();

        globalClient = new Client();
        globalClient.setOneTimeCode(oneTimeCode);
        Utilities.updateGlobalClientDetails(client, globalClient);


        /*Thread sendConfirmationCode = new Thread(new MailSender(client.getEmail(), "Your one time code required for registration: "+oneTimeCode));
        sendConfirmationCode.start();*/

        System.out.println(oneTimeCode);
        return "register_confirmation_code";
    }

    @PostMapping("/register_confirmation_code")
    public String registrationCodeValidation(@ModelAttribute("client") Client client) {


        System.out.println("inserted code: " + client.getOneTimeCode());
        System.out.println("generated code: " + oneTimeCode);

        if (client.getOneTimeCode() == oneTimeCode) {
            userRepository.save(globalUser);
            clientRepository.save(globalClient);

            System.out.println(globalClient);
            new Thread(() -> new MailSender(globalClient.getEmail(), "Your details: \n" + globalClient.toString())).start();
//            Thread sendRegistrationDetails = new Thread(new MailSender(globalClient.getEmail(), "Your details: \n"+ globalClient.toString()));
//            sendRegistrationDetails.start();
            return "register_success";
        } else return "registration_error";

    }





    /* TRANSPORT REGISTRATION*/


    @GetMapping("/register_transport")
    public String registerTransport(Model model) {
        Transport transport = new Transport();
        model.addAttribute("transport", transport);
        model.addAttribute("listOfDestinations", listOfDestinations);
        return "register_transport";
    }


    @PostMapping("/register_transport")
    public String submitTransport(@ModelAttribute("transport") Transport transport, Principal principal) {
        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("User not fount with the name: " + principal.getName()));

        Client client = clientOptional.get();
        //transport.setDriverId(principal.getName()
        transport.setDriverId(client.getEmail());
        transportRepository.save(transport);
        return "register_transport_success";
    }





    /*CHOOSING EXISTING TRANSPORT*/


    @GetMapping("/choose_transport")
    public String showAvailableTransports(Model model, Parcel parcel, Principal principal) {

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("No client with name: " + principal.getName()));

        Client client = clientOptional.get();
        List<Transport> transports = transportRepository.findAll();
        transports.sort(Comparator.comparing(Transport::getDestination).thenComparing(Transport::getDepartureDate));
        model.addAttribute("transports", transports);
        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);

        return "choose_transport";
    }





    /* ADDING PARCEL*/


    @GetMapping("/add_parcel/{id}")
    public String addParcelTmp(@PathVariable("id") Integer id, Model model) {

        Parcel parcel = new Parcel();
        System.out.println("adding parcel form shown");
        transportNumber = id;

        model.addAttribute("parcel", parcel);
        return "add_parcel";

    }

    @PostMapping("/add_parcel")
    public String postParcelTmp(@ModelAttribute("parcel") Parcel parcel, Principal principal) {
        parcel.setUserName(principal.getName());
        Optional<Transport> transportOptional = transportRepository.findById(transportNumber);
        transportOptional.orElseThrow(() -> new RuntimeException("Transport not found"));

        Transport transport = transportOptional.get();
        transport.getParcels().add(parcel);
        transportRepository.save(transport);


        return "client_main";

    }


    /*CLIENT DETAILS*/


    @GetMapping("/client_details")
    public String showClientDetails(Model model, Principal principal) {
        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("No client found"));

        Client client = clientOptional.get();

        model.addAttribute("client", client);

        return "client_details";
    }



    /*EDIT PROFILE*/


    @GetMapping("/edit_profile")
    public String editProfile(Model model, Principal principal) {

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("Something went wrong"));

        Client client = clientOptional.get();
        model.addAttribute("client", client);

        return "edit_profile";

    }

    @PostMapping("/edit_profile")
    public String saveEditProfile(@ModelAttribute("client") Client updatedClient, Principal principal) {

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("Something went wrong"));
        Optional<User> userOptional = userRepository.findByUserName(principal.getName());
        userOptional.orElseThrow(() -> new RuntimeException("user not found"));


        User user = userOptional.get();
        Client oldClient = clientOptional.get();
        Utilities.updateUserPassword(updatedClient, user);
        Utilities.updateClientDetails(oldClient, updatedClient);
        clientRepository.save(updatedClient);
        userRepository.save(user);


        return "success";
    }




    /*EDIT TRANSPORTS*/


}
