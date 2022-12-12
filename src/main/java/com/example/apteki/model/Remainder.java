package com.example.apteki.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Data
@Entity
@Table(name="Reminder")
public class Remainder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "startDate")
    private Date startDate;

    @Column(name = "endDate")
    private Date endDate;

    @Column(name = "time")
    private Time time;

    @Column(name = "count")
    private double count;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="medicament", referencedColumnName = "id")
    Medicament medicament;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="usr", referencedColumnName = "id")
    Usr usr;

    public Remainder(Date startDate, Date endDate, Time time, double count, Medicament medicament, Usr usr) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.time = time;
        this.count = count;
        this.medicament = medicament;
        this.usr = usr;
    }

    public Remainder(){

    }
}
