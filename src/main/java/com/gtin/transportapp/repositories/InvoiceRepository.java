package com.gtin.transportapp.repositories;

import com.gtin.transportapp.models.Client;
import com.gtin.transportapp.models.Invoice;
import com.gtin.transportapp.models.Transport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    Optional<Invoice> findById(Integer id);

}
