package com.example.apteki.repository;

import com.example.apteki.model.PharmacyAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyAddressRepository extends JpaRepository<PharmacyAddress, Integer> {
    List<PharmacyAddress> findByAddress(String address);
}
