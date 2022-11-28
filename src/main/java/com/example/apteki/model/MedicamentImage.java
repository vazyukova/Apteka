package com.example.apteki.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="MedicamentImage")
public class MedicamentImage {
    @Id
    private Integer id;

    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(name="medicament_id", referencedColumnName = "id")
    private Medicament medicament;

    public MedicamentImage(Integer id, String name, Medicament medicament) {
        this.id = id;
        this.name = name;
        this.medicament = medicament;
    }

    public MedicamentImage(){

    }
}
