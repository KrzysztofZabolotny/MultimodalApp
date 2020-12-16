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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utilities {

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
        globalClient.setCompanyName(client.getCompanyName());

        return globalClient;

    }

    public static void updateUserPassword(Client client, User user) {
        user.setPassword(client.getPassword());
    }

    public static String timeStamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
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

    public static int calculateValue(int weight) {

        int group = 0;

        if (weight <= 10) group = -1;
        if (weight > 10 && weight <= 20) group = 0;
        if (weight > 20) group = 1;

        int value = switch (group) {

            case -1 -> 500;
            case 0 -> 700;
            case 1 -> 1000;

            default -> throw new IllegalStateException("Unexpected value:" + group);
        };

        return value;
    }
}
