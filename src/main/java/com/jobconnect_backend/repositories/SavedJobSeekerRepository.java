package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.SavedJobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedJobSeekerRepository extends JpaRepository<SavedJobSeeker, Integer> {
    boolean existsByJobSeekerProfileProfileIdAndCompanyCompanyId(Integer jobSeekerProfileId, Integer companyId);
    Optional<SavedJobSeeker> findByJobSeekerProfileProfileIdAndCompanyCompanyId(Integer jobSeekerProfileId, Integer companyId);
    List<SavedJobSeeker> findByCompanyCompanyId(Integer companyId);
}
