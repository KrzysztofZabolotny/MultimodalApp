package com.gtin.transportapp;

import com.gtin.transportapp.models.User;
import com.gtin.transportapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class MultimodalTransportApp {

    @Autowired
    UserRepository userRepository;


    public static void main(String[] args) {
        SpringApplication.run(MultimodalTransportApp.class, args);


    }

//    @Bean
//    public void makeUser() {
//
//        System.out.println("Making users");
//        for (int i = 0; i <10000 ; i++) {
//            String userName = String.valueOf(Math.random()*1000000);
//            String password = String.valueOf(Math.random()*1000000);
//            userRepository.save(new User(userName, password));
//
//            List<User> userList = userRepository.findAll();
//
//            System.out.println("Number of users "+userList.size());
//        }
//
//
//
//    }

}