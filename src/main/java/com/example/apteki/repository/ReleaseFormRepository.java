package com.example.apteki.repository;

import com.example.apteki.model.ReleaseForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseFormRepository extends JpaRepository<ReleaseForm, Integer> {
    ReleaseForm findByName(String name);
}
