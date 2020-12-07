package com.gtin.transportapp.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilitiesTest {

    @Test
    public void isNotValidEmailAddressTest(){

        String notValidEmailAddress = "email@@email.com";
        assertTrue(Utilities.isNotValidEmailAddress(notValidEmailAddress));

        /*Check HIbernate Validator Test
        * https://github.com/hibernate/hibernate-validator/blob/master/engine/src/test/java/org/hibernate/validator/test/internal/constraintvalidators/hv/EmailValidatorTest.java*/
    }

}