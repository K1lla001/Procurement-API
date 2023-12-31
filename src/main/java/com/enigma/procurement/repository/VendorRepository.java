package com.enigma.procurement.repository;

import com.enigma.procurement.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, String> {

    Optional<Vendor> findVendorById(String id);
}
