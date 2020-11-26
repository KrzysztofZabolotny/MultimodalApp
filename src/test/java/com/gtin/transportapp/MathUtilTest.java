/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp;

import com.gtin.transportapp.models.Client;
import com.gtin.transportapp.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class MathUtilTest {

    @Autowired
    ClientRepository clientRepository;
    @Test
    void testAdd(){
        MathUtils mathUtils = new MathUtils();
        int expected = 2;
        int actual = mathUtils.add(1,1);
        assertEquals(expected,actual, "The method should add two numbers");

    }

    @Test
    void circleArea(){
        MathUtils mathUtils = new MathUtils();
        int expected = 27;
        int actual = mathUtils.circleRadius(3);

        assertEquals(expected, actual);
    }


}
