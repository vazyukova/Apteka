package com.example.apteki.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="MedicamentDosage")
public class MedicamentDosage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(name = "dosage")
    private String dosage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="medicament", referencedColumnName = "id")
    @JsonIgnore
    Medicament medicament;

    public MedicamentDosage()
    {

    }

    public MedicamentDosage(String dosage, Medicament medicament)
    {
        this.dosage = dosage;
        this.medicament = medicament;
    }
}
