package com.example.apteki.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="Medicament")
public class Medicament {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "activeSubstance")
    private String activeSubstance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="category", referencedColumnName = "id")
    Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="manufacturer", referencedColumnName = "id")
    Manufacturer manufacturer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="releaseForm", referencedColumnName = "id")
    ReleaseForm releaseForm;

    public Medicament(String name, String activeSubstance, Category category, Manufacturer manufacturer, ReleaseForm releaseForm)
    {
        this.name = name;
        this.activeSubstance = activeSubstance;
        this.category = category;
        this.manufacturer = manufacturer;
        this.releaseForm = releaseForm;
    }

    public Medicament()
    {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActiveSubstance() {
        return activeSubstance;
    }

    public void setActiveSubstance(String activeSubstance) {
        this.activeSubstance = activeSubstance;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public ReleaseForm getReleaseForm()
    {
        return releaseForm;
    }

    public void setReleaseForm(ReleaseForm releaseForm) {
        this.releaseForm = releaseForm;
    }
}
