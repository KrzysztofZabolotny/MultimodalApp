package com.gtin.transportapp.repositories;

import com.gtin.transportapp.models.PriceRange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriceRangeRepository extends JpaRepository<PriceRange, Integer> {

    Optional<PriceRange> findById(Integer price_id);

}
