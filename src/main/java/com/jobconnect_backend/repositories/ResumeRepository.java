package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    boolean existsByResumeNameAndJobSeekerProfileProfileIdAndDeletedIsFalse(String resumeName, Integer profileId);
}
