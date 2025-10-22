package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByJobJobIdAndJobSeekerProfileProfileId(Integer jobId, Integer profileId);
}
