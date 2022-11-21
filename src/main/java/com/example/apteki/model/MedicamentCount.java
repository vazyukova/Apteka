package com.example.apteki.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="MedicamentCount")
public class MedicamentCount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(name = "count")
    private String count;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="medicament", referencedColumnName = "id")
    @JsonIgnore
    Medicament medicament;

    public MedicamentCount()
    {

    }

    public MedicamentCount(String count, Medicament medicament)
    {
        this.count = count;
        this.medicament = medicament;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
