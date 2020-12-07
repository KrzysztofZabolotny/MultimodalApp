/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.services;

import com.gtin.transportapp.models.Client;
import com.gtin.transportapp.models.User;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public final class Utilities {

    public static int generateRegistrationCode(){

        int bound = 900_000;
        int fill = 100_000;

        return new Random().nextInt(bound)+fill;
    }

    public static Client updateClientDetails(Client previousClientDetails, Client updatedClientDetails){

        updatedClientDetails.setId(previousClientDetails.getId());
        updatedClientDetails.setEmail(previousClientDetails.getEmail());
        updatedClientDetails.setRole(previousClientDetails.getRole());
        updatedClientDetails.setUserName(previousClientDetails.getUserName());

        return updatedClientDetails;

    }public static Client updateGlobalClientDetails(Client client, Client globalClient){

        globalClient.setCity(client.getCity());
        globalClient.setCode(client.getCode());
        globalClient.setEmail(client.getEmail());
        globalClient.setPassword(client.getPassword());
        globalClient.setName(client.getName());
        globalClient.setPassword(client.getPassword());
        globalClient.setPhone(client.getPhone());
        globalClient.setRole(client.getRole());
        globalClient.setStreet(client.getStreet());
        globalClient.setSurname(client.getSurname());
        globalClient.setZip(client.getZip());
        globalClient.setUserName(client.getEmail());

        return globalClient;

    }

    public static User updateUserDetails(Client client ,User user){

        user.setPassword(client.getPassword());

        return user;
    }

    public static String timeStamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static boolean isNotValidEmailAddress(String email) {
        String emailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(emailPattern);
        java.util.regex.Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }
}
