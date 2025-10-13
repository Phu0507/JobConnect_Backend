package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.JobConverter;
import com.jobconnect_backend.dto.dto.JobDTO;
import com.jobconnect_backend.entities.Job;
import com.jobconnect_backend.repositories.*;
import com.jobconnect_backend.services.IJobService;
import com.jobconnect_backend.utils.ValidateField;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements IJobService {
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final JobPositionRepository jobPositionRepository;
    private final ValidateField validateField;
    private final JobConverter jobConverter;
    @Value("${algolia.search.id}")
    private String algoliaID;
    @Value("${algolia.search.key.write}")
    private String algoliaKey;

    private final String indexName = "jobs_index";

    @Override
    public List<JobDTO> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream()
                .map(jobConverter::convertToJobDTO)
                .toList();
    }
}
