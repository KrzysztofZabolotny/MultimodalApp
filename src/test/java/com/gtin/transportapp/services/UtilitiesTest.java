package com.gtin.transportapp.services;

import com.gtin.transportapp.models.PriceRange;
import com.gtin.transportapp.models.Transport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilitiesTest {

    @Test
    public void isNotValidEmailAddressTest(){

        String validEmailAddress = "email@email.com";
        assertTrue(Utilities.isValidEmailAddress(validEmailAddress));

        /*Check HIbernate Validator Test
        * https://github.com/hibernate/hibernate-validator/blob/master/engine/src/test/java/org/hibernate/validator/test/internal/constraintvalidators/hv/EmailValidatorTest.java*/
    }

    @Test
    public void isNotValidPhoneNumber(){

        String fiveDigitNumber = "12345";
        String twelveDigitNumber = "123456789101";
        assertTrue(Utilities.isValidPhoneNumber(fiveDigitNumber));
        assertTrue(Utilities.isValidPhoneNumber(twelveDigitNumber));
    }
    @Test
    public void calculateValue(){

        int weight = 5;
        Transport transport = new Transport();
        transport.getRanges().add(new PriceRange(1,10,500));


        assertEquals(500, Utilities.calculateValue(weight,transport));
    }

}