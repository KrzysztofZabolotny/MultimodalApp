/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.services;

import java.util.Random;

public class Utilities {

    public static int generateRegistrationCode(){

        int bound = 900_000;
        int fill = 1_000_000;

        return new Random().nextInt(bound)+fill;
    }
}
