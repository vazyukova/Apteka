package com.example.apteki.payload;

import com.example.apteki.model.Medicament;

public class SearchResponse {
    private Medicament medicament;
    private double minPrice;
    private int pharmacyCount;

    public SearchResponse(Medicament medicament,
            double minPrice,
            int pharmacyCount)
    {
        this.medicament = medicament;
        this.minPrice = minPrice;
        this.pharmacyCount = pharmacyCount;
    }

    public Medicament getMedicament() {
        return medicament;
    }

    public void setMedicament(Medicament medicament) {
        this.medicament = medicament;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public int getPharmacyCount() {
        return pharmacyCount;
    }

    public void setPharmacyCount(int pharmacyCount) {
        this.pharmacyCount = pharmacyCount;
    }
}
