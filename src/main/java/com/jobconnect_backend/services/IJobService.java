package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.JobDTO;
import com.jobconnect_backend.dto.request.CreateJobRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
public interface IJobService {
    List<JobDTO> getAllJobs();
    void createJob(CreateJobRequest request, BindingResult bindingResult);
}
