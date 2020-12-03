/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.services;

import com.gtin.transportapp.models.Client;

import java.util.Random;

public final class Utilities {

    public static int generateRegistrationCode(){

        int bound = 9_000_000;
        int fill = 1_000_000;

        return new Random().nextInt(bound)+fill;
    }

    public static Client updateClientDetails(Client previousClientDetails, Client updatedClientDetails){

        updatedClientDetails.setId(previousClientDetails.getId());
        updatedClientDetails.setEmail(previousClientDetails.getEmail());
        updatedClientDetails.setPassword(previousClientDetails.getPassword());
        updatedClientDetails.setRole(previousClientDetails.getRole());
        updatedClientDetails.setUserName(previousClientDetails.getUserName());

        return updatedClientDetails;

    }
}
