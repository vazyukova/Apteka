package com.example.apteki.repository;

import com.example.apteki.model.Medicament;
import com.example.apteki.model.MedicamentCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentCountRepository extends JpaRepository<MedicamentCount, Integer> {
    List<MedicamentCount> findByMedicament(Medicament medicament);
    List<MedicamentCount> findByMedicamentAndCount(Medicament medicament, String count);
}
