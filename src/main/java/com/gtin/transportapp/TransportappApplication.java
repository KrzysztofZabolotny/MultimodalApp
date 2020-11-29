package com.gtin.transportapp;

import com.gtin.transportapp.controllers.SendMail;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class TransportappApplication {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(TransportappApplication.class, args);

        for (int i = 1; i <= 60; i++) {
            SendMail.sendMail("krzysztof.zabolotny@gmail.com","Email number: "+ i,timeStamp());
            Thread.sleep(60000);
        }
    }
    public static String timeStamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "Email sent "+dtf.format(now);
    }

}
