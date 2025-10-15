package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.Company;
import com.jobconnect_backend.entities.JobPosition;
import com.jobconnect_backend.entities.JobSeekerProfile;
import com.jobconnect_backend.entities.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Integer> {
    boolean existsByJobSeekerProfileAndCompanyAndJobPosition(JobSeekerProfile jobSeekerProfile, Company company, JobPosition jobPosition);
    boolean existsByJobSeekerProfileProfileId(Integer profileId);
}
