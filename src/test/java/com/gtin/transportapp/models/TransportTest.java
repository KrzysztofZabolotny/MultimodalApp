/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.models;

import com.gtin.transportapp.MathUtils;
import com.gtin.transportapp.models.Client;
import com.gtin.transportapp.models.Transport;
import com.gtin.transportapp.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

    @Test
    void circleArea(){
        MathUtils mathUtils = new MathUtils();
        int expected = 27;
        int actual = mathUtils.circleRadius(3);

        assertEquals(expected, actual);
    }


}
