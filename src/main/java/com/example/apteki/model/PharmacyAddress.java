package com.example.apteki.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="PharmacyAddress")
public class PharmacyAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "address")
    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="pharmacy", referencedColumnName = "id")
    Pharmacy pharmacy;

    public PharmacyAddress(String address, Pharmacy pharmacy)
    {
        this.address = address;
        this.pharmacy = pharmacy;
    }

    public PharmacyAddress()
    {

    }
}
