package com.gtin.transportapp.repositories;

import com.gtin.transportapp.models.Parcel;
import com.gtin.transportapp.models.Transport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransportRepository extends JpaRepository<Transport, Integer> {

    Optional<Transport> findById(Integer id);
}
