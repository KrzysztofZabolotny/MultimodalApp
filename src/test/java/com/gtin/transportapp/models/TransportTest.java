/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TransportTest {

    @Test
    void increaseParcelCountTest(){
        Transport transport = new Transport();

        transport.increaseParcelCount();
        int expected = 1;
        int actual = Integer.parseInt(transport.getNumberOfParcels());
        assertEquals(expected,actual, "The method increases the parcel count");

    }
}
