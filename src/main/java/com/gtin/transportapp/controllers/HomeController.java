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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @GetMapping("/")
    public String home(Principal principal){

        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/edit")
    public String edit(){
        return "edit";
    }

    @GetMapping("/register")
    public String register(Model model){
        User user = new User();

        model.addAttribute("user", user);
        return "register";
    }


    @PostMapping("/register")
    public String submitUser(@ModelAttribute("user") User user){

        userRepository.save(user);
        return "register_success";

    }



    @GetMapping("/register_transport")
    public String registerTransport(Model model,Principal principal){
         Transport transport = new Transport();

        model.addAttribute("transport", transport);
        return "register_transport";
    }


    @PostMapping("/register_transport")
    public String submitTransport(@ModelAttribute("transport") Transport transport, Principal principal){
        transport.setClientId(principal.getName());
        transportRepository.save(transport);
        return "register_transport_success";
    }


    @GetMapping("/register_client")
    public String registerClient(Model model){

        Client client = new Client();

        model.addAttribute(client);

        return "register_client";
    }

    @PostMapping("/register_client")
    public String submitClient(@ModelAttribute("client") Client client){

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
    public String addParcel(Model model,Parcel parcel){

        model.addAttribute("parcel", parcel);
        return "parcel_add";
    }

    @PostMapping("/addparcel")
    public String submitParcel(@ModelAttribute("parcel") Parcel parcel, Principal principal, Model model){


        String userName = principal.getName();
        Optional<Client> clientOptional = clientRepository.findByUserName(userName);
        clientOptional.orElseThrow(()-> new RuntimeException("Nof found: " + userName ));
        Client client = clientOptional.get();
        parcel.setUserName(userName);
        client.getParcels().add(parcel);
        clientRepository.save(client);
        model.addAttribute("parcel",parcel);
        return "parcel_add_success";

    }
    @GetMapping("/view")
    public String view(Principal principal, Model model){

        String userId = principal.getName();
        Optional<Client> clientOptional = clientRepository.findByUserName(userId);
        clientOptional.orElseThrow(()-> new RuntimeException("Nof found: " + userId ));

        model.addAttribute("userId", userId);
        Client client = clientOptional.get();
        model.addAttribute("client", client);

        return "view";
    }

    @GetMapping("/all_transports")
    public String getAllTransports(Model model){

        List<Transport> transports = transportRepository.findAll();

        for (Transport transport: transports){

            System.out.println(transport);
        }

        model.addAttribute("transports",transports);

        return "all_transports";

    }



























    /*@Value("#{${listOfCountries}}")
    private List<String> listOfCountries;
    @Value("#{${listOfBases}}")
    private List<String> listOfBases;
    @Value("#{${listOfDestinations}}")
    private List<String> listOfDestinations;


    @Autowired
    UserRepository userRepository;
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    ParcelRepository parcelRepository;


    @GetMapping("/register_parcel")
    public String showParcelRegistrationForm(Model model) {

        Parcel parcel = new Parcel();
        model.addAttribute("parcel", parcel);
        model.addAttribute("listOfBases", listOfBases);
        model.addAttribute("listOfDestinations", listOfDestinations);
        return "parcel/register_form_parcel";

    }


    @PostMapping("register_parcel")
    public String registerParcel(@ModelAttribute("parcel") Parcel parcel) {
        System.out.println(parcel);
        parcelRepository.save(parcel);

        return "user/user_registration_success_form";
    }*/

//    @GetMapping("/login")
//    public String showLoginForm(Model model) {
//        User user = new User();
//        model.addAttribute("user", user);
//        return "login/login_form";
//    }
//
//    @RequestMapping(method = RequestMethod.POST, value = "/login")
//    public String loginForm(@ModelAttribute("user") User user) {
//        userRepository.save(new User(user.getEmail(), user.getPassword()));
//        return "login/login_success";
//    }


//    @GetMapping("/register")
//    public String showForm(Model model) {
//        Driver driver = new Driver();
//        model.addAttribute("driver", driver);
//        model.addAttribute("countryList", listOfCountries);
//        return "register_form";
//    }
//
//    @RequestMapping(method = RequestMethod.POST, value = "/register")
//    public String submitForm(@ModelAttribute("driver") Driver driver) {
//        driverRepository.save(driver);
//        return "register_success";
//    }
//
//    @GetMapping("/users")
//    public String showAllTransports(Model model){
//        model.addAttribute("users",userRepository.findAll());
//        return "all_users";
//    }
//
//
//    @GetMapping("/login")
//    public String showLoginForm(Model model) {
//        User user = new User();
//        model.addAttribute("user", user);
//        return "login_form";
//    }
//
//    @RequestMapping(method = RequestMethod.POST, value = "/login")
//    public String loginForm(@ModelAttribute("user") User user) {
//        userRepository.save(new User(user.getEmail(), user.getPassword(),user.getRegistrationDate()));
//        return "login_success";
//    }
//
//    @GetMapping("/password_reset")
//    public String forgotPassword(Model model) {
//        User user = new User();
//        model.addAttribute("user", user);
//        return "password_reset_form";
//    }
//
//    @RequestMapping(method = RequestMethod.POST, value = "/password_reset")
//    public String passwordReset(@ModelAttribute("user") User user) {
//
//        return "index";
//    }


}
