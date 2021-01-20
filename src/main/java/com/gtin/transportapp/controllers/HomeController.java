/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.controllers;

import com.gtin.transportapp.models.*;
import com.gtin.transportapp.repositories.*;
import com.gtin.transportapp.services.MailSender;
import com.gtin.transportapp.services.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@EnableScheduling
@Controller
public class HomeController {

    static int transportNumber;

    public static User globalUser;
    public static Client globalClient;
    public static int oneTimeCode;
    public static Transport globalTransport;
    public static Parcel globalParcel;


    @Autowired
    UserRepository userRepository;
    @Autowired
    ParcelRepository parcelRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    TransportRepository transportRepository;
    @Autowired
    PriceRangeRepository priceRangeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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


    @GetMapping("control_panel")
    public String controlPanel(Model model,Principal principal){

        List<Transport> transports = transportRepository.findAll();

        model.addAttribute("transports", transports);

        return "control_panel";
    }

/*
* c7de750 testing
7f18e1b Fixed some design issues on the view part
b68a38b Added BCrypt password encoding
4f72fd2 Added authorization to all methods in the controller
24a6630 Added profile edit for diver
2be7a4b Added authorisation to transport details
5756db1 Added parcel status modification by driver, rounded invoice
d09beda Migrated from H2 to local MySql Server
53e8d92 Backup before reinstaling Intellij
2026a14  Added Transport capacity and load, and method to verify if a package is eligible to be added
8f5fe9d Added Price range funcionality after choosing the date of a new transport
4092dd9 Added PriceRange class, and hard coded some ranges for testing
4eac2c7 Added a confirmation page when adding new parcel
ac7ecd5 Added icons to parcel add form
1b3b4a9 Refacored parcel add form
d1d0fe9 Changed login button to icon, added home icon, and build parcel_details_precise template for more details for a client parcel
0386197 Changed JDK to 14
dd1bc0f Added some icons to the parcel details
38874f1 Added the ability to see every parcel in a created transport
0f9eab5 Added real time countdown timer in available transports, and added icons to transport
06568e6 Added badge with number of transports in driver transports
9677ba0 Merge remote-tracking branch 'origin/cv' into cv
9249035 Added image to inner html files
730c36b Added image to inner html files
fe84c3b added the Company Name in the registration form as driver
65c45a3 Added styling to client parcel
aad2a1c Added your parcels funcionality where all the parcels of a client are displayed
58073b0 Added email and phone number validation and view forms for them
26d48b6 Added email validatin afret submitting the form, bot need to cleare a custom html for it, or create one for all errors, for example wrong_input_in_registration_error.html
95e1b58 Added sending of an email with client registration details
58d2d86 Added a custom error page when the code input does not match the generated one, and added a redirection button to stary the login process again
897f1bd Added code confirmation funcionality to registration process for Client class
3c73adb User and Client password change funcionality added
7aa23e1 Added the logout button to all relevant views
4205271 Added footer to all view files
f793463 Your profile added to client_main.html with with redirecting to edit_profile.html
c9b5091 Database update from edit_profile implemented
10f1dae Added edit_profile funcionality
e7a6e03 Added mode unified app look, but need to redesign the layouts
a5389d2 Implemented Bootstrap Card Columns in choose_transport.html with a custom logo on every transport depending on driverId
6deff4e Card view prototype added to choose_transportTEST.html
d426131 Added new css for index.html with an image
ae436ad Email notification sending implemented in a seperate thread
631a17d Sending email confirmation after New User registration implemented
90b5808 ssl.enapled added to MailSender properties, enabling sending mail from AWS Elastic beanstalk
3374ddf Merge remote-tracking branch 'origin/cv' into cv
6ce3a42 Basic Email notification prototype implemented
b1bf619 Create README.md
e3086dc Method and test added for numberOfParcels parameter in Transport class
6d4355a Adding Parcel to specific available transport implemented, but neet to tweak the show_all_transports.html form
1cd9657 Transport registration made, missing Transport driverId
f6c38dd client_main and driver_main buttons added and centered
dc0f8cd Added redirecting to client or driver in HomeController after login
a40ee46 Fixed issues with registration and notification about unsuccesful login and logout messages
946584f Client registration succesful, but need to create him as a user at the same time
7fa9512 Added index and login html
e534485 Transport class added with it's repository, transports are registering correctrly
4ef2c45  Add Parcel funcionality implemented
9cd3dda (master) Login and user registration in database successful with redirecting to main page
3e2d645 Login and uer registration frame successful*/





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


        Thread sendConfirmationCode = new Thread(new MailSender(client.getEmail(), "Jednorazowy kod wymagany przy rejestracji: " + oneTimeCode));
        sendConfirmationCode.start();

        System.out.println(oneTimeCode);
        return "register_confirmation_code";
    }

    @PostMapping("/register_confirmation_code")
    public String registrationCodeValidation(@ModelAttribute("client") Client client) {
        if (client.getOneTimeCode() == oneTimeCode) {
            globalUser.setPassword(passwordEncoder.encode(globalUser.getPassword()));
            globalClient.setPassword(globalUser.getPassword());
            userRepository.save(globalUser);
            clientRepository.save(globalClient);
            Thread sendRegistrationDetails;
            if (globalClient.getRole().equals("Client")) {
                sendRegistrationDetails = new Thread(new MailSender(globalClient.getEmail(), "Twoje dane: \n" + globalClient.toString()));
            } else {
                sendRegistrationDetails = new Thread(new MailSender(globalClient.getEmail(), "Twoje dane: \n" + globalClient.toString() + "\n" + "Company name: " + globalClient.getCompanyName()));
            }
            sendRegistrationDetails.start();
            return "register_success";
        } else return "registration_error";

    }


    /* TRANSPORT REGISTRATION*/


    @GetMapping("/register_transport")
    public String registerTransport(Model model) {
        Transport transport = new Transport();
        model.addAttribute("transport", transport);
        model.addAttribute("destination", transport.getDestination());
        //model.addAttribute("listOfDestinations", listOfDestinations);
        return "register_transport";
    }


    @PostMapping("/register_transport")
    public String submitTransport(@ModelAttribute("transport") Transport transport, Principal principal) {


        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("User not fount with the name: " + principal.getName()));

        PriceRange priceRange1 = new PriceRange();
        PriceRange priceRange2 = new PriceRange();
        PriceRange priceRange3 = new PriceRange();
        PriceRange priceRange4 = new PriceRange();
        PriceRange priceRange5 = new PriceRange();

        transport.getPriceRanges().add(priceRange1);
        transport.getPriceRanges().add(priceRange2);
        transport.getPriceRanges().add(priceRange3);
        transport.getPriceRanges().add(priceRange4);
        transport.getPriceRanges().add(priceRange5);

        Client client = clientOptional.get();
        transport.setDriverId(client.getEmail());
        transport.setCompanyName(client.getCompanyName());

        globalTransport = transport;


        return "register_transport_price_range";
    }

    @PostMapping("/register_transport_price_range")
    public String submitTransportPriceRanges(@ModelAttribute("transport") Transport transport, @RequestParam(required = false) String add) {

        globalTransport.setPriceRanges(transport.getPriceRanges());
        globalTransport.setCapacity(transport.getCapacity());

        globalTransport.getPriceRanges().removeIf(p -> p.getPrice() == 0 && p.getFromWeight() == 0 && p.getToWeight() == 0);
        for (PriceRange priceRange : globalTransport.getPriceRanges()) {
            priceRangeRepository.save(priceRange);
        }
        transportRepository.save(globalTransport);

        try {


            System.out.println("Twój transport został dołączony do naszej bazy\n"
                    + "Szczegóły transportu:\n" + globalTransport.transportSummary());


            Thread thread = new Thread(new MailSender(globalTransport.getDriverId(), "Twój transport został dołączony do naszej bazy\n"
                    + "Szczegóły transportu:\n" + globalTransport.transportSummary()));

            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "successful_transport_registration";
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

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        Client client = clientOptional.get();

        Transport transport = transportOptional.get();
        parcel.setUserName(principal.getName());
        parcel.setDestination(transport.getDestination());
        parcel.setDepartureDate(transport.getDepartureDate());
        parcel.setValue(Utilities.calculateValue(parcel.getWeight(), transport));
        parcel.setInTransportNumber(transportNumber);
        parcel.setInTransportName(transport.getCompanyName());
        parcel.setOwnerPhoneNumber(client.getCode() + client.getPhone());
        parcel.setOwnerAddress(client.getStreet() + " " + client.getZip() + " " + client.getCity());
        parcel.setOwnerName(client.getName() + " " + client.getSurname());
        parcel.setOwnerEmail(client.getEmail());
        globalParcel = parcel;
        transport.getParcels().add(parcel);
        transport.increaseParcelCount();

        if (transport.permitLoading(parcel.getWeight())) {
            transport.setBallast(transport.getBallast() + parcel.getWeight());
        } else return "error_permit_load";
        globalClient = client;
        globalTransport = transport;

        return "add_parcel_confirmation";

    }

    @PostMapping("/add_parcel_confirmation")
    public String addParcelConfirmation(@ModelAttribute("parcel") Parcel parcel) {

        transportRepository.save(globalTransport);
        try {
            Thread thread = new Thread(new MailSender(globalClient.getEmail(), "Twoja paczka została dodana do transportu\n"
                    + "Dane paczki\n"
                    + globalParcel.parcelSummary()));

            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return "successful_parcel_add";
    }

    @GetMapping("/parcel_details_precise/{id}")
    public String parcelDetails(@PathVariable("id") int id, Model model) {

        Optional<Parcel> parcelOptional = parcelRepository.findById(id);
        parcelOptional.orElseThrow(() -> new RuntimeException("Not found"));

        Parcel parcel = parcelOptional.get();

        Optional<Transport> transportOptional = transportRepository.findById(parcel.getInTransportNumber());
        transportOptional.orElseThrow(() -> new RuntimeException("Transport not found"));

        Transport transport = transportOptional.get();

        Optional<Client> clientOptional = clientRepository.findByUserName(transport.getDriverId());

        clientOptional.orElseThrow(() -> new RuntimeException("Driver not found"));

        Client client = clientOptional.get();


        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);
        model.addAttribute("transport", transport);

        return "parcel_details_precise";

    }





    /*SHOWING PARCELS*/


    @GetMapping("/client_parcels")
    public String showClientParcels(Model model, Principal principal) {


        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

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

    @GetMapping("/driver_details")
    public String showDriverDetails(Model model, Principal principal) {
        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("No client found"));

        Client client = clientOptional.get();

        model.addAttribute("client", client);

        return "driver_details";
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

        updatedClient.setPassword(passwordEncoder.encode(updatedClient.getPassword()));
        Utilities.updateClientDetails(oldClient, updatedClient);
        user.setPassword(updatedClient.getPassword());
        clientRepository.save(updatedClient);
        userRepository.save(user);


        return "successful_profile_edit";
    }

    @GetMapping("/parcel_details/{id}")
    public String showParcelDetails(@PathVariable("id") Integer id, Model model, Principal principal) {

        Optional<Transport> transportOptional = transportRepository.findById(id);
        transportOptional.orElseThrow(() -> new RuntimeException("not found"));
        Transport transport = transportOptional.get();

        if (!transport.getDriverId().equals(principal.getName())) return "error_403_unauthorised";

        List<Parcel> parcels = transport.getParcels();

        model.addAttribute("parcels", parcels);
        return "parcel_details";

    }

    @GetMapping("/transport_details/{id}")
    public String showTransportDetails(@PathVariable("id") Integer id, Model model, Principal principal) {

        Optional<Transport> transportOptional = transportRepository.findById(id);
        transportOptional.orElseThrow(() -> new RuntimeException("not found"));
        Transport transport = transportOptional.get();

        if (!transport.getDriverId().equals(principal.getName())) return "error_403_unauthorised";

        List<Parcel> parcels = transport.getParcels();

        int transportValue = 0;
        double transportVolume = 0;
        int transportWeight = Utilities.calculateWeight(parcels);

        for (Parcel p : parcels) {
            transportValue += p.getValue();
            transportVolume += Utilities.calculateVolume(p);
        }

        double invoice = Utilities.calculateInvoice(transportValue);

        model.addAttribute("parcels", parcels);
        model.addAttribute("parcels", parcels);
        model.addAttribute("transportValue", transportValue);
        model.addAttribute("transportVolume", transportVolume);
        model.addAttribute("transportWeight", transportWeight);
        model.addAttribute("invoice", invoice);
        return "transport_details";

    }

    @GetMapping("/delete_transport/{id}")
    public String deleteParcel(@PathVariable("id") Integer id, Principal principal) {

        Optional<Transport> transportOptional = transportRepository.findById(id);
        transportOptional.orElseThrow(() -> new RuntimeException("not found"));

        Transport transport = transportOptional.get();

        if (!transport.getDriverId().equals(principal.getName())) return "error_403_unauthorised";

        globalTransport = transportOptional.get();


        return "delete_transport_confirmation";

    }

    @PostMapping("/delete_transport_confirmation")
    public String deleteParcelConfirmation(Principal principal) {


        Transport transport = globalTransport;

        if (!transport.getDriverId().equals(principal.getName())) return "error_403_unauthorised";

        try {
            new MailSender(globalTransport.getDriverId(), "Transport został usunięty\n"
                    + globalTransport.transportSummary()).sendMail();
        } catch (Exception e) {
            e.printStackTrace();
        }
        transportRepository.delete(globalTransport);


        return "successful_transport_deletion";
    }

    @GetMapping("/transport_delivered_notification/{id}")
    public String deliverTransport(@PathVariable("id") Integer id, Principal principal) {

        Optional<Transport> transportOptional = transportRepository.findById(id);
        transportOptional.orElseThrow(() -> new RuntimeException("not found"));
        Transport transport = transportOptional.get();

        if (!transport.getDriverId().equals(principal.getName())) return "error_403_unauthorised";
        Thread notifyAboutStatusChange;
        for (Parcel parcel : transport.getParcels()) {

            parcel.setStatus("DOSTARCZONA");

            notifyAboutStatusChange = new Thread(new MailSender(parcel.getOwnerEmail(), "Twoja paczka została dostarczona"));
            notifyAboutStatusChange.start();


        }
        transportRepository.save(transport);
        return "transport_delivered_notification";
    }


    @GetMapping("/accept_parcel/{id}")
    public String acceptParcel(@PathVariable("id") Integer id, Principal principal) {

        Optional<Parcel> parcelOptional = parcelRepository.findById(id);
        parcelOptional.orElseThrow(() -> new RuntimeException("Parcel not found"));

        Parcel parcel = parcelOptional.get();
        Optional<Transport> transportOptional = transportRepository.findById(parcel.getInTransportNumber());
        transportOptional.orElseThrow(() -> new RuntimeException("Transport not found"));

        Transport transport = transportOptional.get();
        if (!transport.getDriverId().equals(principal.getName())) return "error_403_unauthorised";

        parcel.setStatus("ZATWIERDZONA");
        Thread notifyAboutStatusChange;
        notifyAboutStatusChange = new Thread(new MailSender(parcel.getOwnerEmail(), "Twoja paczka na adres:\n"
                + parcel.getAddress()
                + "\n" + parcel.getZip()
                + "\n" + parcel.getCity()
                + "\n" + parcel.getCountry()
                + " została zatwierdzona przez: "
                + transport.getDriverId()
                + "z firmy" + transport.getCompanyName()));
        notifyAboutStatusChange.start();

        parcelRepository.save(parcel);
        return "/successful_parcel_acceptance";

    }

    @GetMapping("/deny_parcel/{id}")
    public String denyParcel(@PathVariable("id") Integer id, Principal principal) {

        Optional<Parcel> parcelOptional = parcelRepository.findById(id);
        parcelOptional.orElseThrow(() -> new RuntimeException("Parcel not found"));

        Parcel parcel = parcelOptional.get();
        Optional<Transport> transportOptional = transportRepository.findById(parcel.getInTransportNumber());
        transportOptional.orElseThrow(() -> new RuntimeException("Transport not found"));

        Transport transport = transportOptional.get();
        if (!transport.getDriverId().equals(principal.getName())) return "error_403_unauthorised";

        parcel.setStatus("ODRZUCONY");
        Thread notifyAboutStatusChange;
        notifyAboutStatusChange = new Thread(new MailSender(parcel.getOwnerEmail(), "Twoja paczka została odrzucona"));
        notifyAboutStatusChange.start();


        transport.setNumberOfParcels(transport.getNumberOfParcels() - 1);
        parcelRepository.delete(parcel);
        return "/successful_parcel_denial";

    }

    @GetMapping("/accept_all_parcels/{id}")
    public String acceptAllParcels(@PathVariable("id") Integer id, Principal principal) {

        Optional<Transport> transportOptional = transportRepository.findById(id);
        transportOptional.orElseThrow(() -> new RuntimeException("Transport not found"));

        Transport transport = transportOptional.get();


        if (!transport.getDriverId().equals(principal.getName())) return "error_403_unauthorised";


        Thread notifyAboutStatusChange;
        for (Parcel parcel : transport.getParcels()) {

            parcel.setStatus("ZATWIERDZONY");

            notifyAboutStatusChange = new Thread(new MailSender(parcel.getOwnerEmail(), "Twoja paczka została dostarczona"));
            notifyAboutStatusChange.start();


        }

        transportRepository.save(transport);
        return "/successful_parcel_acceptance_all";

    }


    @GetMapping("/generate_report/{id}")
    public ResponseEntity<Object> generateTransportWaybill(@PathVariable("id") Integer id, Principal principal) throws IOException {

        Optional<Transport> transportOptional = transportRepository.findById(id);
        transportOptional.orElseThrow(() -> new RuntimeException("Not found"));
        Transport transport = transportOptional.get();

        if (!transport.getDriverId().equals(principal.getName())) {

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("notauthorised.txt"));
                writer.write("Brak autoryzacji");
                writer.close();

            } catch (Exception e) {

                e.printStackTrace();

            }

            String filename = "notauthorised.txt";
            File file = new File(filename);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition",
                    String.format("attachment; filename=\"%s\"", file.getName()));
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");


            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                }
            });
            return ResponseEntity.ok().headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/txt")).body(resource);
        }

        String transportData = "src/main/resources/reports/" + transport.getCompanyName() + transport.getDepartureDate();


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(transportData + ".txt"));
            writer.write(Utilities.generateTransportWaybill(transport));
            writer.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        String filename = transportData + ".txt";
        File file = new File(filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition",
                String.format("attachment; filename=\"%s\"", file.getName()));
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });

        return ResponseEntity.ok().headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/txt")).body(resource);
    }





    /*EDIT TRANSPORTS*/



    /*SCHEDUlED TASKS*/

//    static int counter = 0;
//    @Scheduled(fixedDelay = 100)
//    public static void runner(){
//
//        Thread thread = new Thread(new Timer());
//        thread.start();
//
//    }


}


