package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, Integer> {
    List<ApplicationStatusHistory> findByApplicationApplicationId(Integer applicationId);
}
