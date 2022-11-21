package com.example.apteki.repository;

import com.example.apteki.model.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, Integer> {
    public List<Medicament> findByName(String name);
    public List<Medicament> findByActiveSubstance(String activeSubstance);
}
