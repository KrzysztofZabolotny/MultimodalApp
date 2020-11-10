/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.controller;

import com.gtin.transportapp.models.User;
import com.gtin.transportapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {


    @Autowired
    UserRepository userRepository;


    @GetMapping("/")
    public String home(){
        return "hello";
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


    @GetMapping("/view/{userId}")
    public String view(@PathVariable String userId, Model model){
        model.addAttribute("userId", userId);
        return "profile";
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
