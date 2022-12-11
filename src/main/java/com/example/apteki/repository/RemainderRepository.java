package com.example.apteki.repository;

import com.example.apteki.model.Remainder;
import com.example.apteki.model.Usr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RemainderRepository extends JpaRepository<Remainder, Integer> {
    List<Remainder> findByUsr(Usr user);
}
