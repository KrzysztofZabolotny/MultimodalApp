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

        for (int i = 1; i < 10; i++) {

            MailSender.sendMail("krzysztof.zabolotny@gmail.com","Mail number: "+i,timeStamp());
        }

    }

    public static String timeStamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "User created "+dtf.format(now);
    }



}
