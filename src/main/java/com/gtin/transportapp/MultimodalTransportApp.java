package com.gtin.transportapp;

import com.gtin.transportapp.services.MailSender;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class MultimodalTransportApp {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(MultimodalTransportApp.class, args);


    }


}
