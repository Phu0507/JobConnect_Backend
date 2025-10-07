package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobCategoryRepository extends JpaRepository<JobCategory, Integer> {
    boolean existsByName(String name);
}
