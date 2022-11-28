package com.example.apteki.repository;

import com.example.apteki.model.Medicament;
import com.example.apteki.model.MedicamentDosage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentDosageRepository extends JpaRepository<MedicamentDosage, Integer> {
    List<MedicamentDosage> findByMedicament(Medicament medicament);
    List<MedicamentDosage> findByMedicamentAndDosage(Medicament medicament, String dosage);
}
