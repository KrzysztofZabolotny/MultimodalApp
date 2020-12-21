/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.repositories;

import com.gtin.transportapp.models.PriceRange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RangeRepository extends JpaRepository<PriceRange, Integer> {

    Optional<PriceRange> findById(Integer id);

}
