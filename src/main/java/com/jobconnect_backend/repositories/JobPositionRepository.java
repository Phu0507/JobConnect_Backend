package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPositionRepository extends JpaRepository<JobPosition, Integer> {
}
