package com.gtin.transportapp.repositories;

import com.gtin.transportapp.models.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParcelRepository extends JpaRepository<Parcel, Integer> {

    Optional<Parcel> findByUserName(String username);
}
