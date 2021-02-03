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
import org.springframework.web.servlet.ModelAndView;

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
    public static Passenger globalPassenger;


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
    InvoiceRepository invoiceRepository;
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${listOfDestinations}")
    private List<String> listOfDestinations;
    @Value("#{'${possibleNumberOfSeats}'.split(',')}")
    private List<Integer> possibleNumberOfSeats;





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
    public String controlPanel(Model model, Principal principal) {

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
                sendRegistrationDetails = new Thread(new MailSender(globalClient.getEmail(), "Twoje dane: \n" + globalClient.clientSummary()));
            } else {
                sendRegistrationDetails = new Thread(new MailSender(globalClient.getEmail(), "Twoje dane: \n" + globalClient.clientSummary() + "\n" + "Company name: " + globalClient.getCompanyName()));
            }
            sendRegistrationDetails.start();
            return "register_success";
        } else return "registration_error";

    }


    /* TRANSPORT REGISTRATION*/


    @GetMapping("/choose_transport_type")
    public String chooseTransportType(Model model) {

        return "choose_transport_type";
    }
    @GetMapping("/choose_transport_type_driver")
    public String chooseDriverTransportType(Model model) {

        return "choose_transport_type_driver";
    }
    @GetMapping("/choose_transport_type_client")
    public String chooseClientTransportType(Model model) {

        return "choose_transport_type_client";
    }

    @GetMapping("/choose_new_transport_type_client")
    public String chooseNewTransportTypeClient(Model model) {

        return "choose_new_transport_type_client";
    }

    @GetMapping("/register_passenger_transport")
    public String registerPassengerTransport(Model model) {
        Transport transport = new Transport();

        //List<Integer> possibleNumberOfSeats = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));
        model.addAttribute("transport", transport);
        model.addAttribute("destination", transport.getDestination());
        model.addAttribute("possibleNumberOfSeats", possibleNumberOfSeats);
        return "register_passenger_transport";
    }

    @PostMapping("/register_passenger_transport")
    public String submitPassengerTransport(@ModelAttribute("transport") Transport transport, Principal principal) {


        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("User not fount with the name: " + principal.getName()));
        Client client = clientOptional.get();
        transport.setDriverId(client.getEmail());
        transport.setCompanyName(client.getCompanyName());
        transport.setDriverPhoneNumber(client.getCode() + "" + client.getPhone());
        transport.setStatus("AKTYWNY");
        transport.setDesignation("passenger");
        transport.setNumberOfPassengers(0);

        System.out.println("pasazerowie: " + transport.getNumberOfPassengers());
        System.out.println("siedzenia: " + transport.getNumberOfSeats());
        transportRepository.save(transport);


        return "successful_transport_registration";
    }


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
        transport.setDriverPhoneNumber(client.getCode() + "" + client.getPhone());

        globalTransport = transport;


        return "register_transport_price_range";
    }

    @PostMapping("/register_transport_price_range")
    public String submitTransportPriceRanges(@ModelAttribute("transport") Transport transport, @RequestParam(required = false) String add) {

        globalTransport.setPriceRanges(transport.getPriceRanges());
        globalTransport.setCapacity(transport.getCapacity());
        globalTransport.setStatus("PRZYJMUJE PACZKI");
        globalTransport.setDesignation("parcel");


        globalTransport.getPriceRanges().removeIf(p -> p.getPrice() == 0 && p.getFromWeight() == 0 && p.getToWeight() == 0);
        for (PriceRange priceRange : globalTransport.getPriceRanges()) {
            priceRangeRepository.save(priceRange);
        }
        transportRepository.save(globalTransport);

        try {

            Thread thread = new Thread(new MailSender(globalTransport.getDriverId(), "Twój transport został dołączony do naszej bazy\n"
                    + "Szczegóły transportu:\n" + globalTransport.transportSummary()));
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "successful_transport_registration";
    }

    /*CHOOSING EXISTING TRANSPORT*/


    @GetMapping("/choose_parcel_transport")
    public String showAvailableParcelTransports(Model model, Parcel parcel, Principal principal) {

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("No client with name: " + principal.getName()));

        Client client = clientOptional.get();
        List<Transport> transports = transportRepository.findAll();


        transports.sort(Comparator.comparing(Transport::getDestination).thenComparing(Transport::getDepartureDate));
        transports = transports.stream()
                .filter(transport -> !transport.getStatus().equals("dostarczony"))
                .filter(transport -> transport.getDesignation().equals("parcel"))
                .collect(Collectors.toList());

        if (transports.size() == 0) {
            return "no_transports_in_repository";
        }
//        List<Transport> transportsOut = new ArrayList<>();
//        for (Transport t: transports){
//
//            if(!t.getStatus().equals("dostarczony"))transportsOut.add(t);
//        }
        model.addAttribute("transports", transports);
        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);


        return "choose_transport";
    }

    @GetMapping("/choose_passenger_transport")
    public String showAvailablePassengerTransports(Model model, Parcel parcel, Principal principal) {

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("No client with name: " + principal.getName()));

        Client client = clientOptional.get();
        List<Transport> transports = transportRepository.findAll();


        transports.sort(Comparator.comparing(Transport::getDestination).thenComparing(Transport::getDepartureDate));
        transports = transports.stream()
                .filter(transport -> !transport.getStatus().equals("dostarczony"))
                .filter(transport -> transport.getDesignation().equals("passenger"))
                .collect(Collectors.toList());

        if (transports.size() == 0) {
            return "no_transports_in_repository";
        }
//        List<Transport> transportsOut = new ArrayList<>();
//        for (Transport t: transports){
//
//            if(!t.getStatus().equals("dostarczony"))transportsOut.add(t);
//        }
        model.addAttribute("transports", transports);
        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);


        return "choose_passenger_transport";
    }

    @GetMapping("/choose_transport")
    public String showAvailableTransports(Model model, Parcel parcel, Principal principal) {

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("No client with name: " + principal.getName()));

        Client client = clientOptional.get();
        List<Transport> transports = transportRepository.findAll();


        transports.sort(Comparator.comparing(Transport::getDestination).thenComparing(Transport::getDepartureDate));
        transports = transports.stream().filter(transport -> !transport.getStatus().equals("dostarczony")).collect(Collectors.toList());

        if (transports.size() == 0) {
            return "no_transports_in_repository";
        }
//        List<Transport> transportsOut = new ArrayList<>();
//        for (Transport t: transports){
//
//            if(!t.getStatus().equals("dostarczony"))transportsOut.add(t);
//        }
        model.addAttribute("transports", transports);
        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);


        return "choose_transport";
    }

    @GetMapping("/driver_parcel_transports")
    public String showDriverParcelTransports(Model model, Parcel parcel, Principal principal) {

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("No client with name: " + principal.getName()));

        Client client = clientOptional.get();
        List<Transport> transports = transportRepository.findAll();
        List<Transport> driverTransports = transports.stream()
                .filter(t -> t.getDriverId().equals(client.getUserName()))
                .filter(transport -> !transport.getStatus().equals("dostarczony"))
                .filter(transport -> transport.getDesignation().equals("parcel"))
                .collect(Collectors.toList());

        if (driverTransports.size() == 0) {
            return "no_transports_in_repository";
        }
        model.addAttribute("transports", driverTransports);
        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);

        return "driver_parcel_transports";
    }


    @GetMapping("/driver_passenger_transports")
    public String showDriverPassengerTransports(Model model, Parcel parcel, Principal principal) {

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("No client with name: " + principal.getName()));

        Client client = clientOptional.get();
        List<Transport> transports = transportRepository.findAll();
        List<Transport> driverTransports = transports.stream()
                .filter(t -> t.getDriverId().equals(client.getUserName()))
                .filter(transport -> !transport.getStatus().equals("dostarczony"))
                .filter(transport -> transport.getDesignation().equals("passenger"))
                .collect(Collectors.toList());

        if (driverTransports.size() == 0) {
            return "no_transports_in_repository";
        }
        model.addAttribute("transports", driverTransports);
        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);

        return "driver_passenger_transports";
    }

    @GetMapping("/transports_history")
    public String showTransportsHistory(Model model, Parcel parcel, Principal principal) {

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("No client with name: " + principal.getName()));

        Client client = clientOptional.get();
        List<Transport> transports = transportRepository.findAll();
        List<Transport> driverTransports = transports.stream().filter(t -> t.getDriverId().equals(client.getUserName())).filter(transport -> transport.getStatus().equals("dostarczony")).collect(Collectors.toList());

        if (driverTransports.size() == 0) {
            return "no_transports_in_repository";
        }
        model.addAttribute("transports", driverTransports);
        model.addAttribute("parcel", parcel);
        model.addAttribute("client", client);

        return "driver_transports";
    }

    @GetMapping("parcel_history")
    public String showParcelHistory(Model model, Principal principal) {
        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        Client client = clientOptional.get();

        List<Parcel> parcels = parcelRepository.findAll();

        List<Parcel> clientParcels = parcels.stream().filter(p -> p.getUserName().equals(client.getUserName())).filter(parcel -> parcel.getStatus().equals("DOSTARCZONA")).collect(Collectors.toList());

        if (clientParcels.size() == 0) {
            return "no_parcels_registered";
        }
        model.addAttribute("parcels", clientParcels);
        return "client_parcels";
    }

    @GetMapping("show_transport_type_client")
    public String showTransportTypeClient(){

        return "show_transport_type_client";
    }






    /* ADDING PARCEL*/


    @GetMapping("/add_parcel/{id}")
    public String addParcel(@PathVariable("id") Integer id, Model model) {

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
        parcel.setDriverEmail(transport.getDriverId());
        parcel.setDriverPhoneNumber(transport.getDriverPhoneNumber());
        parcel.setOwner(client.getName() + " " + client.getSurname());
        parcel.setOwnerPhoneNumber(client.getCode() + " " + client.getPhone());
        globalParcel = parcel;
        transport.getParcels().add(parcel);
        transport.increaseParcelCount();

        if (transport.permitLoading(parcel.getWeight())) {
            transport.setBallast(transport.getBallast() + parcel.getWeight());
        } else return "error_permit_load";
        transport.setPassengers(new ArrayList<>());
        globalClient = client;
        globalTransport = transport;

        return "add_parcel_confirmation";

    }

    @PostMapping("/add_parcel_confirmation")
    public String addParcelConfirmation(@ModelAttribute("parcel") Parcel parcel) {

        System.out.println(globalTransport);
        System.out.println();
        System.out.println();
        System.out.println(parcel);

        transportRepository.save(globalTransport);
        try {
            Thread thread = new Thread(new MailSender(globalClient.getEmail(), "Twoja paczka została dodana do transportu\n"
                    + "Dane paczki:\n\n"
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

    @GetMapping("/passenger_details_precise/{id}")
    public String passengerDetails(@PathVariable("id") int id, Model model) {

        Optional<Passenger> passengerOptional = passengerRepository.findById(id);
        passengerOptional.orElseThrow(() -> new RuntimeException("Not found"));


        Passenger passenger = passengerOptional.get();

        Optional<Transport> transportOptional = transportRepository.findById(passenger.getInTransportNumber());
        transportOptional.orElseThrow(() -> new RuntimeException("Transport not found"));

        Transport transport = transportOptional.get();

        Optional<Client> clientOptional = clientRepository.findByUserName(transport.getDriverId());

        clientOptional.orElseThrow(() -> new RuntimeException("Driver not found"));

        Client client = clientOptional.get();


        model.addAttribute("passenger", passenger);
        model.addAttribute("client", client);
        model.addAttribute("transport", transport);

        return "passenger_details_precise";

    }




    /* ADDING PASSENGER*/


    @GetMapping("/add_passenger/{id}")
    public String addPassenger(@PathVariable("id") Integer id, Model model) {

        Passenger passenger = new Passenger();
        transportNumber = id;

        model.addAttribute("passenger", passenger);
        return "add_passenger";

    }
    @PostMapping("/add_passenger")
    public String postPassenger(@ModelAttribute("passenger") Passenger passenger, Principal principal) {

        Optional<Transport> transportOptional = transportRepository.findById(transportNumber);
        transportOptional.orElseThrow(() -> new RuntimeException("Transport not found"));

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        Client client = clientOptional.get();

        Transport transport = transportOptional.get();
        passenger.setUserName(principal.getName());
        passenger.setDestination(transport.getDestination());
        passenger.setDepartureDate(transport.getDepartureDate());
        passenger.setInTransportNumber(transportNumber);
        passenger.setInTransportName(transport.getCompanyName());
        passenger.setDriverEmail(transport.getDriverId());
        passenger.setDriverPhoneNumber(transport.getDriverPhoneNumber());
        passenger.setOwner(client.getName() + " " + client.getSurname());
        passenger.setOwnerPhoneNumber(client.getCode() + " " + client.getPhone());
        globalPassenger = passenger;
        transport.getPassengers().add(passenger);

        globalClient = client;
        globalTransport = transport;

        return "add_passenger_confirmation";

    }

    @PostMapping("/add_passenger_confirmation")
    public String addPassengerConfirmation(@ModelAttribute("passenger") Passenger passenger) {
        globalTransport.increasePassengerCount();
        globalTransport.decreaseNumberOfSeats();

        transportRepository.save(globalTransport);
//        try {
//            Thread thread = new Thread(new MailSender(globalClient.getEmail(), "Twoja paczka została dodana do transportu\n"
//                    + "Dane paczki:\n\n"
//                    + globalParcel.parcelSummary()));
//
//            thread.start();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        return "successful_passenger_add";
    }






    /*SHOWING PARCELS*/


    @GetMapping("/client_parcels")
    public String showClientParcels(Model model, Principal principal) {


        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        Client client = clientOptional.get();

        List<Parcel> parcels = parcelRepository.findAll();

        List<Parcel> clientParcels = parcels.stream().filter(p -> p.getUserName().equals(client.getUserName())).filter(parcel -> !parcel.getStatus().equals("DOSTARCZONA")).collect(Collectors.toList());


        if (clientParcels.size() == 0) {
            return "no_parcels_registered";
        }
        model.addAttribute("parcels", clientParcels);
        return "client_parcels";
    }

    @GetMapping("/client_passengers")
    public String showClientPassengers(Model model, Principal principal) {


        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());
        clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        Client client = clientOptional.get();

        List<Passenger> passengers = passengerRepository.findAll();

        List<Passenger> clientPassengers = passengers.stream().filter(passenger -> passenger.getUserName().equals(client.getUserName())).filter(passenger -> !passenger.getStatus().equals("DOSTARCZONY")).collect(Collectors.toList());

        if (clientPassengers.size() == 0) {
            return "no_passengers_registered";
        }
        model.addAttribute("passengers", clientPassengers);
        return "client_passengers";
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
        List<Passenger> passengers = transport.getPassengers();

        if(passengers.size()!=0){

            int transportValue = 0;
            for (Passenger passenger: passengers){
                transportValue+=transport.getTicketValue();
            }

            double invoice = Utilities.calculateInvoice(transportValue);
            model.addAttribute("passenger", passengers);
            model.addAttribute("transportValue", transportValue);
            model.addAttribute("invoice", invoice);
            return "transport_details_passenger";

        }

        if (parcels.size() == 0 && passengers.size() == 0 ) {
            return "no_items_in_transport";
        }

        int transportValue = 0;
        double transportVolume = 0;
        int transportWeight = Utilities.calculateWeight(parcels);

        for (Parcel p : parcels) {
            transportValue += p.getValue();
            transportVolume += Utilities.calculateVolume(p);
        }

        double invoice = Utilities.calculateInvoice(transportValue);

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
            Thread thread = new Thread(new MailSender(globalTransport.getDriverId(), "Transport został usunięty\n"
                    + globalTransport.transportSummary()));
            thread.start();

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
            notifyAboutStatusChange = new Thread(new MailSender(parcel.getUserName(), "Twoja paczka została dostarczona"));
            notifyAboutStatusChange.start();

        }
        invoiceRepository.save(new Invoice().populateAfterDelivery(transport));
        transport.setStatus("dostarczony");
        transportRepository.save(transport);
        return "transport_delivered_notification";
    }


    @GetMapping("/transport_left_notification/{id}")
    public String leftTransport(@PathVariable("id") Integer id, Principal principal) {

        Optional<Transport> transportOptional = transportRepository.findById(id);
        transportOptional.orElseThrow(() -> new RuntimeException("not found"));
        Transport transport = transportOptional.get();

        if (!transport.getDriverId().equals(principal.getName())) return "error_403_unauthorised";
        Thread notifyAboutStatusChange;
        for (Parcel parcel : transport.getParcels()) {

            parcel.setStatus("W DRODZE");
            notifyAboutStatusChange = new Thread(new MailSender(parcel.getUserName(), "Twoja paczka na adres:\n"
                    + parcel.getReceiverStreet()
                    + "\n" + parcel.getReceiverCity()
                    + "\n" + parcel.getReceiverZip()
                    + "\n" + parcel.getReceiverCountry()
                    + "\nz planowanym wyjazdem:\n"
                    + transport.getDepartureDate()
                    + "\njest już w drodze."
                    + "\n\nKontakt do kierowcy: "
                    + "\n " + transport.getDriverId()
                    + "\n " + transport.getDriverPhoneNumber()
                    + "\nz firmy " + transport.getCompanyName()
                    + "\n\n W razie pytań skontaktuj się z kierowcą"));
            notifyAboutStatusChange.start();
        }
        invoiceRepository.save(new Invoice().populateAfterDelivery(transport));
        transport.setStatus("W DRODZE");
        transportRepository.save(transport);
        return "transport_left_notification";
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
        notifyAboutStatusChange = new Thread(new MailSender(parcel.getUserName(), "Twoja paczka na adres:\n"
                + parcel.getReceiverStreet()
                + "\n" + parcel.getReceiverCity()
                + "\n" + parcel.getReceiverZip()
                + "\n" + parcel.getReceiverCountry()
                + "\nz planowanym wyjazdem: \n"
                + transport.getDepartureDate()
                + "\nzostała zatwierdzona przez:"
                + "\n " + transport.getDriverId()
                + "\n " + transport.getDriverPhoneNumber()
                + "\nz firmy" + transport.getCompanyName()
                + "\n\n W razie pytań skontaktuj się z kierowcą"));
        notifyAboutStatusChange.start();
        parcelRepository.save(parcel);
        return "accept_parcel";

    }

    @GetMapping("/accept_passenger/{id}")
    public String acceptPassenger(@PathVariable("id") Integer id, Principal principal) {

        Optional<Passenger> passengerOptional = passengerRepository.findById(id);
        passengerOptional.orElseThrow(() -> new RuntimeException("Passenger not found"));

        Passenger passenger = passengerOptional.get();
        Optional<Transport> transportOptional = transportRepository.findById(passenger.getInTransportNumber());
        transportOptional.orElseThrow(() -> new RuntimeException("Transport not found"));

        Transport transport = transportOptional.get();
        if (!transport.getDriverId().equals(principal.getName())) return "error_403_unauthorised";

        passenger.setStatus("ZATWIERDZONY");
        Thread notifyAboutStatusChange;
        notifyAboutStatusChange = new Thread(new MailSender(passenger.getUserName(), "Twój transport na adres:\n"
                + passenger.getDestinationStreet()
                + "\n" + passenger.getDestinationCity()
                + "\n" + passenger.getDestinationZip()
                + "\n" + passenger.getDestinationCountry()
                + "\nz planowanym wyjazdem: \n"
                + transport.getDepartureDate()
                + "\nzostał zatwierdzony przez:"
                + "\n " + transport.getDriverId()
                + "\n " + transport.getDriverPhoneNumber()
                + "\nz firmy" + transport.getCompanyName()
                + "\n\n W razie pytań skontaktuj się z kierowcą"));
        notifyAboutStatusChange.start();
        passengerRepository.save(passenger);
        return "accept_passenger";

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
        notifyAboutStatusChange = new Thread(new MailSender(parcel.getUserName(), "Twoja paczka na adres:\n"
                + parcel.getReceiverStreet()
                + "\n" + parcel.getReceiverCity()
                + "\n" + parcel.getReceiverZip()
                + "\n" + parcel.getReceiverCountry()
                + "\nz planowanym wyjazdem: \n"
                + transport.getDepartureDate()
                + "\nzostała odrzucona przez: \n"
                + transport.getDriverId()
                + "\nz firmy " + transport.getCompanyName()
                + "\n\n W razie pytań skontaktuj się z kierowcą"));
        notifyAboutStatusChange.start();


        transport.setNumberOfParcels(transport.getNumberOfParcels() - 1);
        parcelRepository.delete(parcel);
        return "deny_parcel";

    }

    @GetMapping("/deny_passenger/{id}")
    public String denyPassenger(@PathVariable("id") Integer id, Principal principal) {

        Optional<Passenger> passengerOptional = passengerRepository.findById(id);
        passengerOptional.orElseThrow(() -> new RuntimeException("Parcel not found"));

        Passenger passenger = passengerOptional.get();
        Optional<Transport> transportOptional = transportRepository.findById(passenger.getInTransportNumber());
        transportOptional.orElseThrow(() -> new RuntimeException("Transport not found"));

        Transport transport = transportOptional.get();
        if (!transport.getDriverId().equals(principal.getName())) return "error_403_unauthorised";

        passenger.setStatus("ODRZUCONY");
        Thread notifyAboutStatusChange;
        notifyAboutStatusChange = new Thread(new MailSender(passenger.getUserName(), "Twój transport na adres:\n"
                + passenger.getDestinationStreet()
                + "\n" + passenger.getDestinationCity()
                + "\n" + passenger.getDestinationZip()
                + "\n" + passenger.getDestinationCountry()
                + "\nz planowanym wyjazdem: \n"
                + transport.getDepartureDate()
                + "\nzostał odrzucony przez:"
                + "\n " + transport.getDriverId()
                + "\n " + transport.getDriverPhoneNumber()
                + "\nz firmy" + transport.getCompanyName()
                + "\n\n W razie pytań skontaktuj się z kierowcą"));
        notifyAboutStatusChange.start();


        transport.setNumberOfPassengers(transport.getNumberOfPassengers() - 1);
        passengerRepository.delete(passenger);
        return "deny_passenger";

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

            notifyAboutStatusChange = new Thread(new MailSender(parcel.getUserName(), "Twoja paczka na adres:\n"
                    + parcel.getReceiverStreet()
                    + "\n" + parcel.getReceiverCity()
                    + "\n" + parcel.getReceiverZip()
                    + "\n" + parcel.getReceiverCountry()
                    + "\nz planowanym wyjazdem: \n"
                    + transport.getDepartureDate()
                    + "\nzostała zatwierdzona przez:"
                    + "\n " + transport.getDriverId()
                    + "\n " + transport.getDriverPhoneNumber()
                    + "\nz firmy" + transport.getCompanyName()
                    + "\n\nW razie pytań skontaktuj się z kierowcą"));
            notifyAboutStatusChange.start();


        }

        transportRepository.save(transport);
        return "successful_parcel_acceptance_all";

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

        String transportData = transport.getCompanyName() + transport.getDepartureDate();


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

    @GetMapping("/terms_of_service")
    public String terms_of_service() {

        return "terms_of_service";
    }

    @GetMapping("/messages")
    public String showAllMessages(Model model, Principal principal){


        List<Message> messages = messageRepository.findAll();

        Optional<Client> clientOptional = clientRepository.findByUserName(principal.getName());

        Client client = clientOptional.get();

        List<Message> clientMessages = messages.stream()
                .filter(message -> message.getFromUserName()
                        .equals(client.getUserName())||message.getToUserName()
                        .equals(client.getUserName()))
                .collect(Collectors.toList());



        model.addAttribute("time", Utilities.timeStamp());
        model.addAttribute("messages", clientMessages);
        model.addAttribute("client", client);

        return "messages";
    }




    /*RESET PASSWORD*/

    // Display the form
    @RequestMapping(value="/forgot_password", method=RequestMethod.GET)
    public ModelAndView displayResetPassword(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("forgot_password");
        return modelAndView;
    }

    // Receive the address and send an email
    @RequestMapping(value="/forgot_password", method=RequestMethod.POST)
    public ModelAndView forgotUserPassword(ModelAndView modelAndView, User user) throws Exception {
        Optional<User> optionalUser = userRepository.findByUserName(user.getUserName());
        optionalUser.orElseThrow(()-> new RuntimeException("No user found"));

        User existingUser = optionalUser.get();
        if (existingUser != null) {
            // Create token

             ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);

            confirmationTokenRepository.save(confirmationToken);

            Thread sendMail = new Thread(new MailSender( existingUser.getUserName(),"W celu zresetowania hasła, kliknij na link: "
                    + "http://localhost:8080/confirm-reset?token="+confirmationToken.getConfirmationToken()));
            sendMail.start();

            modelAndView.addObject("message", "Request to reset password received. Check your inbox for the reset link.");
            modelAndView.setViewName("successForgotPassword");

        } else {
            modelAndView.addObject("message", "This email address does not exist!");
            modelAndView.setViewName("error_incorrect_email");
        }
        return modelAndView;
    }

    @RequestMapping(value="/confirm-reset", method= {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView validateResetToken(ModelAndView modelAndView, @RequestParam("token")String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {

            Optional<User> userOptional = userRepository.findByUserName(token.getUser().getUserName());

            User user = userOptional.get();
            user.setActive(true);
            userRepository.save(user);
            modelAndView.addObject("user", user);
            modelAndView.addObject("emailId", user.getId());
            modelAndView.setViewName("reset_password");
        } else {
            modelAndView.addObject("message", "The link is invalid or broken!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    // Endpoint to update a user's password
    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    public ModelAndView resetUserPassword(ModelAndView modelAndView, User user) {
        if (user.getUserName() != null) {
            // Use email to find user

            Optional<User> userOptional = userRepository.findByUserName(user.getUserName());

            User tokenUser = userOptional.get();

            tokenUser.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(tokenUser);
            modelAndView.addObject("message", "Password successfully reset. You can now log in with the new credentials.");
            modelAndView.setViewName("password_reset_success");
        } else {
            modelAndView.addObject("message","The link is invalid or broken!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }


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


