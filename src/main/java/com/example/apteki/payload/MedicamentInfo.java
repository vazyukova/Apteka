package com.example.apteki.payload;

import com.example.apteki.model.Medicament;
import com.example.apteki.model.MedicamentAvailability;
import com.example.apteki.model.MedicamentCount;
import com.example.apteki.model.MedicamentDosage;

import java.util.List;

public class MedicamentInfo {
    private List<MedicamentCount> medicamentCounts;
    private List<MedicamentDosage> medicamentDosages;

    public MedicamentInfo(List<MedicamentCount> medicamentCounts, List<MedicamentDosage> medicamentDosages) {
        this.medicamentCounts = medicamentCounts;
        this.medicamentDosages = medicamentDosages;
    }

    public List<MedicamentCount> getMedicamentCounts() {
        return medicamentCounts;
    }

    public void setMedicamentCounts(List<MedicamentCount> medicamentCounts) {
        this.medicamentCounts = medicamentCounts;
    }

    public List<MedicamentDosage> getMedicamentDosages() {
        return medicamentDosages;
    }

    public void setMedicamentDosages(List<MedicamentDosage> medicamentDosages) {
        this.medicamentDosages = medicamentDosages;
    }
}
