package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.JobDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IJobService {
    List<JobDTO> getAllJobs();
}
