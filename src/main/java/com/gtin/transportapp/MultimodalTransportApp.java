package com.gtin.transportapp;

import com.gtin.transportapp.models.User;
import com.gtin.transportapp.repositories.UserRepository;
import com.gtin.transportapp.services.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class MultimodalTransportApp {


    public static void main(String[] args) throws Exception {

        SpringApplication.run(MultimodalTransportApp.class, args);


    }
}