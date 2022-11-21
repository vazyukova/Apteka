package com.example.apteki.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="MedicamentAvailability")
public class MedicamentAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "price")
    private Double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="medicamentCount", referencedColumnName = "id")
    MedicamentCount medicamentCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="medicamentDosage", referencedColumnName = "id")
    MedicamentDosage medicamentDosage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="pharmacyAddress", referencedColumnName = "id")
    PharmacyAddress pharmacyAddress;

    public MedicamentAvailability()
    {

    }

    public MedicamentAvailability(Double price, MedicamentCount medicamentCount, MedicamentDosage medicamentDosage, PharmacyAddress pharmacyAddress)
    {
        this.price = price;
        this.medicamentCount = medicamentCount;
        this.medicamentDosage = medicamentDosage;
        this.pharmacyAddress = pharmacyAddress;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
