package com.gtin.transportapp.repositories;

import com.gtin.transportapp.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    Optional<Client> findByUserName(String userName);

}
