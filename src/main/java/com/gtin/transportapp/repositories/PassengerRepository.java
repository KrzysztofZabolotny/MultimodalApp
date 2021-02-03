package com.gtin.transportapp.repositories;

import com.gtin.transportapp.models.Client;
import com.gtin.transportapp.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Integer> {

    Optional<Passenger> findByUserName(String userName);

}
