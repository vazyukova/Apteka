package com.example.apteki.repository;

import com.example.apteki.model.Medicament;
import com.example.apteki.model.MedicamentImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicamentImageRepository extends JpaRepository<MedicamentImage, Integer> {
    List<MedicamentImage> findByMedicament(Medicament medicament);
}
