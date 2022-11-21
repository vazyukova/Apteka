package com.example.apteki.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="Pharmacy")
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "site")
    private String site;

    public Pharmacy()
    {

    }

    public Pharmacy(String name, String site)
    {
        this.name = name;
        this.site = site;
    }
}
