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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.stream.Collectors;
@EnableScheduling
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
    /*@Autowired
    private PasswordEncoder passwordEncoder;*/

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
            if (clientOptional.isPresent()) return "errors/error_user_already_exists";
            if (!Utilities.isValidEmailAddress(client.getEmail())) return "errors/error_incorrect_email";
            if (!Utilities.isValidPhoneNumber(client.getPhone())) return "errors/error_incorrect_phone_number";
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
        /*    globalUser.setPassword(passwordEncoder.encode(globalUser.getPassword()));*/
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
        transport.setDriverId(client.getEmail());
        transport.setCompanyName(client.getCompanyName());
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

    @GetMapping("/driver_transports")
    public String showDriverTransports(Model model, Parcel parcel, Principal principal) {

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("No client with name: " + principal.getName()));

        Client client = clientOptional.get();
        List<Transport> transports = transportRepository.findAll();
        List<Transport> driverTransports = transports.stream().filter(t -> t.getDriverId().equals(client.getUserName())).collect(Collectors.toList());
        model.addAttribute("transports", driverTransports);
        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);

        return "driver_transports";
    }





    /* ADDING PARCEL*/


    @GetMapping("/add_parcel/{id}")
    public String addParcelTmp(@PathVariable("id") Integer id, Model model) {

        Parcel parcel = new Parcel();
        transportNumber = id;

        model.addAttribute("parcel", parcel);
        return "add_parcel";

    }

    @PostMapping("/add_parcel")
    public String postParcelTmp(@ModelAttribute("parcel") Parcel parcel, Principal principal) {

        Optional<Transport> transportOptional = transportRepository.findById(transportNumber);
        transportOptional.orElseThrow(() -> new RuntimeException("Transport not found"));

        Transport transport = transportOptional.get();
        parcel.setUserName(principal.getName());;
        parcel.setDestination(transport.getDestination());
        parcel.setDepartureDate(transport.getDepartureDate());
        parcel.setValue(Utilities.calculateValue(parcel.getWeight()));
        parcel.setInTransportNumber(transportNumber);
        transport.getParcels().add(parcel);
        transport.increaseParcelCount();
        transportRepository.save(transport);


        return "client_main";

    }

    @GetMapping("/parcel_details_precise/{id}")
    public String parcelDetails(@PathVariable("id") int id, Model model){

        Optional<Parcel> parcelOptional = parcelRepository.findById(id);
        parcelOptional.orElseThrow(()-> new RuntimeException("Not found"));

        Parcel parcel = parcelOptional.get();

        Optional<Transport> transportOptional = transportRepository.findById(parcel.getInTransportNumber());
        transportOptional.orElseThrow(() -> new RuntimeException("Transport not found"));

        Transport transport = transportOptional.get();

        Optional<Client> clientOptional = clientRepository.findByUserName(transport.getDriverId());

        clientOptional.orElseThrow(()-> new RuntimeException("Driver not found"));

        Client client = clientOptional.get();


        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);
        model.addAttribute("transport", transport);

        return "parcel_details_precise";

    }





    /*SHOWING PARCELS*/





    @GetMapping("/client_parcels")
    public String showClientParcels(Model model, Principal principal){



        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(()-> new RuntimeException("Client not found"));

        Client client = clientOptional.get();

        List<Parcel> parcels = parcelRepository.findAll();

        List<Parcel> clientParcels = parcels.stream().filter(p -> p.getUserName().equals(client.getUserName())).collect(Collectors.toList());

        model.addAttribute("parcels", clientParcels);

        return "client_parcels";
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



    @GetMapping("/parcel_details/{id}")
    public String showParcelDetails(@PathVariable("id") Integer id, Model model) {

        Optional<Transport> transportOptional = transportRepository.findById(id);
        transportOptional.orElseThrow(()-> new RuntimeException("not found"));
        Transport transport = transportOptional.get();

        List<Parcel> parcels = transport.getParcels();

        model.addAttribute("parcels", parcels);
        return "parcel_details";

    }
    @GetMapping("/transport_details/{id}")
    public String showTransportDetails(@PathVariable("id") Integer id, Model model) {

        Optional<Transport> transportOptional = transportRepository.findById(id);
        transportOptional.orElseThrow(()-> new RuntimeException("not found"));
        Transport transport = transportOptional.get();

        List<Parcel> parcels = transport.getParcels();

        int transportValue = 0;
        int transportVolume = 0;
        int transportWeight = Utilities.calculateWeight(parcels);

        for (Parcel p: parcels){
            transportValue+=p.getValue();
            transportVolume+=Utilities.calculateVolume(p);
        }

        double invoice = Utilities.calculateInvoice(transportValue);
        System.out.println("Transport value: "+transportValue);
        System.out.println("invoice: "+invoice);

        model.addAttribute("parcels", parcels);
        model.addAttribute("transportValue", transportValue);
        model.addAttribute("transportVolume", transportVolume);
        model.addAttribute("transportWeight", transportWeight);
        model.addAttribute("invoice", invoice);
        return "transport_details";

    }

    @GetMapping("/delete_parcel/{id}")
    public String deleteParcel(@PathVariable("id") Integer id) {

        Optional<Transport> transportOptional = transportRepository.findById(id);
        transportOptional.orElseThrow(()-> new RuntimeException("not found"));
        Transport transport = transportOptional.get();
        transportRepository.delete(transport);//delete transport
        return "driver_transports";

    }




    /*EDIT TRANSPORTS*/



    /*SCHEDUlED TASKS*/

//    @Scheduled(fixedDelay = 1000)
//    public static void runner(){
//        System.out.println(Utilities.timeStamp());
//        System.out.println(Thread.activeCount());
//        Thread sendRegistrationDetails = new Thread(new MailSender("krzysztof.zabolotny@gmail.com", Utilities.timeStamp()));
//        sendRegistrationDetails.start();
//    }

}
