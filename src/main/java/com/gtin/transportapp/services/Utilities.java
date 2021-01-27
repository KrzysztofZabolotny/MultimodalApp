/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.services;

import com.gtin.transportapp.models.*;
import com.gtin.transportapp.configurations.SecurityConfiguration;
import com.gtin.transportapp.repositories.ParcelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utilities {

    @Autowired
    PasswordEncoder passwordEncoder;


    public static int generateRegistrationCode() {

        int bound = 900_000;
        int fill = 100_000;

        return new Random().nextInt(bound) + fill;
    }

    public static Client updateClientDetails(Client previousClientDetails, Client updatedClientDetails) {

        updatedClientDetails.setId(previousClientDetails.getId());
        updatedClientDetails.setEmail(previousClientDetails.getEmail());
        updatedClientDetails.setRole(previousClientDetails.getRole());
        updatedClientDetails.setUserName(previousClientDetails.getUserName());

        return updatedClientDetails;

    }

    public static Client updateGlobalClientDetails(Client client, Client globalClient) {


        globalClient.setUserName(client.getEmail());
        globalClient.setName(client.getName());
        globalClient.setSurname(client.getSurname());
        globalClient.setEmail(client.getEmail());
        globalClient.setPassword(SecurityConfiguration.getPasswordEncoder().encode(client.getPassword()));
        globalClient.setCode(client.getCode());
        globalClient.setPhone(client.getPhone());
        globalClient.setRole(client.getRole());

//        globalClient.setStreet(client.getStreet());
//        globalClient.setZip(client.getZip());
//        globalClient.setCity(client.getCity());
//        globalClient.setPassword(client.getPassword());

        globalClient.setCompanyName(client.getCompanyName());

        return globalClient;

    }

    public static void updateUserPassword(Client client, User user) {

        client.setPassword(SecurityConfiguration.getPasswordEncoder().encode(user.getPassword()));
    }

    public static String timeStamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static LocalDateTime todayDate(){
        return LocalDateTime.now();
    }

    public static boolean isValidEmailAddress(String email) {
        String emailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {

        String patterns = ("^\\d{5}$" + "|^\\d{6}$" + "|^\\d{7}$" + "|^\\d{8}$" + "|^\\d{9}$" + "|^\\d{10}$" + "|^\\d{11}$" + "|^\\d{12}$");
        Pattern pattern = Pattern.compile(patterns);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static int calculateValue(int weight, Transport transport) {

        Collection<PriceRange> priceRanges = transport.getPriceRanges();


        for (PriceRange r: priceRanges){
            if(weight>=r.getFromWeight() && weight<=r.getToWeight()) return r.getPrice();
        }

        return 0;
    }

    public static int calculateVolume(Parcel parcel){

        int width = parcel.getWidth();
        int length = parcel.getLength();
        int height = parcel.getHeight();

        return width*length*height;
    }

    public static int calculateWeight(List<Parcel> parcels){

        int totalWeight = 0;

        for(Parcel p: parcels){
            totalWeight+=p.getWeight();
        }

        return totalWeight;
    }

    public static double calculateInvoice(int value){

        double interestRate = 0.05;

        return Math.round(value*interestRate);
    }

    public static String generateTransportWaybill(Transport transport){


        List<Parcel> parcels = transport.getParcels();

        String report = "";

        for (Parcel parcel: parcels){



            report+="Dane klienta składającego zamówienie\n";
            report+= "Imie i Nazwisko: "+parcel.getOwner()+"\n";
            report+="Numer telefonu: "+parcel.getOwnerPhoneNumber()+"\n\n";


            report+="Dane nadawcy\n\n";
            report+="Imie i Nazwisko: "+parcel.getSenderName()+" "+parcel.getSenderSurname()+"\n";
            report+="Adres: "+parcel.getSenderStreet()+", "+parcel.getSenderCity()+", "+parcel.getSenderZip()+", "+parcel.getSenderCountry()+"\n";
            report+="Numer telefonu: "+parcel.getSenderPhoneNumber()+"\n\n";

            report+="Dane odbiorcy\n\n";
            report+="Imie i Nazwisko: "+parcel.getReceiverName()+" "+parcel.getReceiverSurname()+"\n";
            report+="Adres: "+parcel.getReceiverStreet()+", "+parcel.getReceiverCity()+", "+parcel.getReceiverZip()+", "+parcel.getReceiverCountry()+"\n";
            report+="Numer telefonu: "+parcel.getReceiverPhoneNumber()+"\n\n";


            report+="Dane paczki:\n";
            report+="waga: "+parcel.getWeight()
                    +"kg, wsokość: "+parcel.getHeight()
                    +"cm, szerokość: "+parcel.getWidth()
                    +"cm, długość " +parcel.getLength()
                    +"cm\n\n";
            report+="Zawartość: "+parcel.getContent()+"\n";
            report+="Komentarz: "+parcel.getAdditionalComments()+"\n\n";
            report+="Deklarowana wartość: "+parcel.getDeclaredValue()+"Zł\n";
            report+="Kwota do zapłaty: "+parcel.getValue()+"Nok\n";
            report+="_____________________________________________________________________________________________________________________________________\n";

        }

        return report;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
