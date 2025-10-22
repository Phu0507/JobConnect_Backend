package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.CardCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardCompanyRepository extends JpaRepository<CardCompany, Integer> {
    CardCompany findByUserUserId(Integer userId);
}
