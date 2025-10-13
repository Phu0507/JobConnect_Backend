package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Integer> {
}
