package com.example.apteki.repository;

import com.example.apteki.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentAvailabilityRepository extends JpaRepository<MedicamentAvailability, Integer> {
    public List<MedicamentAvailability> findByMedicamentCountAndMedicamentDosage(MedicamentCount medicamentCount, MedicamentDosage medicament);
    List<MedicamentAvailability> findByMedicamentCount(MedicamentCount medicamentCount);
    List<MedicamentAvailability> findByMedicamentDosage(MedicamentDosage medicamentDosage);
}
