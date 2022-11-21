package com.example.apteki.payload;

import com.example.apteki.model.Medicament;

public class SearchRequest {
    private String name;
    private String category;
    private String activeSubstance;
    private String manufacturerName;
    private String country;
    private String releaseForm;
    private Double dosage;

    public SearchRequest(String name,
            String category,
            String activeSubstance,
            String manufacturerName,
            String country,
            String releaseForm,
            Double dosage)
    {
        this.name = name;
        this.category = category;
        this.activeSubstance = activeSubstance;
        this.manufacturerName = manufacturerName;
        this.country = country;
        this.releaseForm = releaseForm;
        this.dosage = dosage;
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

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getReleaseForm() {
        return releaseForm;
    }

    public void setReleaseForm(String releaseForm) {
        this.releaseForm = releaseForm;
    }

    public Double getDosage() {
        return dosage;
    }

    public void setDosage(Double dosage) {
        this.dosage = dosage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
