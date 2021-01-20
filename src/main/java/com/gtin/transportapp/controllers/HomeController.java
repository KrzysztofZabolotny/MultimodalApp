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


