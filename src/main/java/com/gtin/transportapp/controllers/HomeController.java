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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.*;

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


    @GetMapping("show_all_transports")
    public String showZoo(Model model) {

        List<Transport> transports = transportRepository.findAll();

        model.addAttribute("transports", transports);

        return "show_all_transports";

    }


    @GetMapping("/register")
    public String registerClient(Model model) {

        Client client = new Client();
        model.addAttribute(client);

        return "register";
    }

    @PostMapping("/register")
    public String submitClient(@ModelAttribute("client") Client client) throws Exception {

        User user = new User();
        user.setPassword(client.getPassword());
        user.setUserName(client.getEmail());
        user.setActive(true);
        user.setRoles("USER");
        client.setUserName(client.getEmail());
        userRepository.save(user);
        clientRepository.save(client);
        Thread sendConfirmation = new Thread(new MailSender(client.getEmail(), MailSender.timeStamp()));
        sendConfirmation.start();
        return "register_success";
    }


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

    @GetMapping("/choose_transportTEST")
    public String showAvailableTransports(Model model, Parcel parcel, Principal principal) {

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("No client with name: " + principal.getName()));

        Client client = clientOptional.get();
        List<Transport> transports = transportRepository.findAll();
        Collections.sort(transports, Comparator.comparing(Transport::getDestination).thenComparing(Transport::getDepartureDate));
        int orderNumber = 1;
        for (Transport t : transports) {
            t.setOrderNumber(orderNumber);
            orderNumber++;
        }
        model.addAttribute("transports", transports);
        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);

        return "choose_transportTEST";
    }

    @GetMapping("/add_parcel_tmp/{id}")
    public String addParcelTmp(@PathVariable("id") Integer id, Model model, Transport transport) {

        Parcel parcel = new Parcel();
        System.out.println("adding parcel form shown");
        transportNumber = id;

        model.addAttribute("parcel", parcel);
        return "add_parcel_tmp";

    }

    @PostMapping("/add_parcel_tmp")
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

//    @GetMapping("/view")
//    public String view(Principal principal, Model model) {
//
//        String userId = principal.getName();
//        Optional<Client> clientOptional = clientRepository.findByUserName(userId);
//        clientOptional.orElseThrow(() -> new RuntimeException("Nof found: " + userId));
//
//        model.addAttribute("userId", userId);
//        Client client = clientOptional.get();
//        model.addAttribute("client", client);
//
//        return "view";
//    }

//    @GetMapping("/all_transports")
//    public String getAllTransports(Model model) {
//
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();
//        String date = dtf.format(now);
//
//        List<Transport> transports = transportRepository.findAll();
//        model.addAttribute("transports", transports);
//        model.addAttribute("date", date);
//
//
//        return "transports_all";
//
//    }
//    @GetMapping("/show_one_transport/{id}")
//    public String showOneTransport(@PathVariable("id") Integer id, Model model, Parcel parcel){
//        Optional<Transport> optionalTransport = transportRepository.findById(id);
//        optionalTransport.orElseThrow(()-> new RuntimeException("No transport with id: "+id));
//
//        Transport transport = optionalTransport.get();
//
//
//        model.addAttribute("transport", transport);
//        System.out.println(transport);
//
//        return "show_one_transport";
//    }


}
