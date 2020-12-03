/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.controllers;

import com.gtin.transportapp.models.Client;
import com.gtin.transportapp.models.Parcel;
import com.gtin.transportapp.models.Transport;
import com.gtin.transportapp.models.User;
import com.gtin.transportapp.repositories.ClientRepository;
import com.gtin.transportapp.repositories.ParcelRepository;
import com.gtin.transportapp.repositories.TransportRepository;
import com.gtin.transportapp.repositories.UserRepository;
import com.gtin.transportapp.services.MailSender;
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


        if (client.getRole().equals("Driver")) return "driver_main";
        if (client.getRole().equals("Client")) return "client_main";
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

        User user = new User();
        user.setPassword(client.getPassword());
        user.setUserName(client.getEmail());
        client.setUserName(client.getEmail());
        userRepository.save(user);
        clientRepository.save(client);
        Thread sendConfirmationCode = new Thread(new MailSender(client.getEmail(), MailSender.timeStamp()));
        sendConfirmationCode.start();
        return "register_success";
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
        //transport.setDriverId(principal.getName());
        transport.setNumberOfParcels("0");
        transport.setOrderNumber(0);
        transport.setDriverId(principal.getName());
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
        int orderNumber = 1;
        for (Transport t : transports) {
            t.setOrderNumber(orderNumber);
            orderNumber++;
        }
        model.addAttribute("transports", transports);
        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);

        return "choose_transport";
    }





    /* ADDING PARCEL*/





    @GetMapping("/add_parcel/{id}")
    public String addParcelTmp(@PathVariable("id") Integer id, Model model, Transport transport) {

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
        transport.increaseParcelCount();
        transport.getParcels().add(parcel);
        transportRepository.save(transport);


        return "client_main";

    }




    /*EDIT PROFILE*/



    @GetMapping("/edit_profile")
    public String editProfile(Model model, Principal principal){

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(()-> new RuntimeException("Something went wrong"));

        Client client = clientOptional.get();
        clientRepository.delete(client);
        model.addAttribute("client", client);

        return "edit_profile";

    }

    @PostMapping("/edit_profile")
    public String saveEditProfile(@ModelAttribute("client") Client client){


        clientRepository.save(client);

        return "success";
    }


}
