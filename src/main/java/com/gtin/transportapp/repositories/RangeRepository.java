/**
 * Created by Krzysztof Zabolotny, https://github.com/KrzysztofZabolotny
 */
package com.gtin.transportapp.repositories;

import com.gtin.transportapp.models.Client;
import com.gtin.transportapp.models.Range;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RangeRepository extends JpaRepository<Range, Integer> {

    Optional<Range> findById(Integer id);

}
