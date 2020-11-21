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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {


    @Autowired
    UserRepository userRepository;
    @Autowired
    ParcelRepository parcelRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    TransportRepository transportRepository;

    @Value("${listOfProvinces}")
    private String[] listOfProvinces;


    @GetMapping("/")
    public String home() {


        List<Parcel> parcels = new ArrayList<>();
        Parcel parcel1 = new Parcel(1, "Foo", "Tomatoes", "20", "20", "20", "20");
        Parcel parcel2 = new Parcel(2, "Bar", "Oranges", "20", "20", "20", "20");
        Parcel parcel3 = new Parcel(3, "Bar", "Apples", "20", "20", "20", "20");

        parcels.add(parcel1);
        parcels.add(parcel2);
        parcels.add(parcel3);

        Transport transport1 = new Transport(1, LocalDate.of(2020, 1, 1), "Berlin", "Foo");
        Transport transport2 = new Transport(2,LocalDate.of(2023,3,1),"Berlin","Bar");
        Transport transport3 = new Transport(3,LocalDate.of(2026,4,1),"Berlin","Bar");

        transport1.getParcels().clear();
        transport1.getParcels().add(parcel1);
        transport1.getParcels().add(parcel2);
        transport1.getParcels().add(parcel3);

//        transport2.getParcels().clear();
//        transport2.getParcels().add(parcel1);
//        transport2.getParcels().add(parcel2);
//        transport2.getParcels().add(parcel3);
//
//        transport3.getParcels().clear();
//        transport3.getParcels().add(parcel1);
//        transport3.getParcels().add(parcel2);
//        transport3.getParcels().add(parcel3);

        transportRepository.save(transport1);
        transportRepository.save(transport2);
        transportRepository.save(transport3);


        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/register")
    public String register(Model model) {
        User user = new User();

        model.addAttribute("user", user);
        return "register";
    }


    @PostMapping("/register")
    public String submitUser(@ModelAttribute("user") User user) {

        userRepository.save(user);
        return "register_success";

    }


    @GetMapping("/register_transport")
    public String registerTransport(Model model, Principal principal) {
        Transport transport = new Transport();

        model.addAttribute("transport", transport);
        return "register_transport";
    }


    @PostMapping("/register_transport")
    public String submitTransport(@ModelAttribute("transport") Transport transport, Principal principal) {
        transport.setClientId(principal.getName());
        transportRepository.save(transport);
        return "register_transport_success";
    }


    @GetMapping("/register_client")
    public String registerClient(Model model) {

        Client client = new Client();
        //model.addAttribute("listOfProvinces", listOfProvinces);
        model.addAttribute(client);

        return "register_client";
    }

    @PostMapping("/register_client")
    public String submitClient(@ModelAttribute("client") Client client) {

        User user = new User();
        user.setUserName(client.getUserName());
        user.setPassword(client.getPassword());
        user.setActive(true);
        user.setRoles("USER");
        userRepository.save(user);
        clientRepository.save(client);
        return "client_register_success";
    }

    @GetMapping("/addparcel")
    public String addParcel(Model model, Parcel parcel) {

        model.addAttribute("parcel", parcel);
        return "parcel_add";
    }

    @PostMapping("/addparcel")
    public String submitParcel(@ModelAttribute("parcel") Parcel parcel, Principal principal, Model model) {


        String userName = principal.getName();
        Optional<Client> clientOptional = clientRepository.findByUserName(userName);
        clientOptional.orElseThrow(() -> new RuntimeException("Nof found: " + userName));
        Client client = clientOptional.get();
        parcel.setUserName(userName);
        client.getParcels().add(parcel);
        clientRepository.save(client);
        model.addAttribute("parcel", parcel);
        return "parcel_add_success";

    }

    @GetMapping("/view")
    public String view(Principal principal, Model model) {

        String userId = principal.getName();
        Optional<Client> clientOptional = clientRepository.findByUserName(userId);
        clientOptional.orElseThrow(() -> new RuntimeException("Nof found: " + userId));

        model.addAttribute("userId", userId);
        Client client = clientOptional.get();
        model.addAttribute("client", client);

        return "view";
    }

    @GetMapping("/all_transports")
    public String getAllTransports(Model model) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        List<Transport> transports = transportRepository.findAll();
        model.addAttribute("transports", transports);
        model.addAttribute("date", date);


        return "all_transports";

    }

    @GetMapping("/style")
    public String style(){
        return "test";
    }

}
