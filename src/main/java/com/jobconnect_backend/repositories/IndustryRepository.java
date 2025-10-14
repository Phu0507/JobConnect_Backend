package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.Industry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndustryRepository extends JpaRepository<Industry, Integer> {
    boolean existsByName(String name);
}
