package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.dto.response.JobCategoryResponse;
import com.jobconnect_backend.entities.JobCategory;
import com.jobconnect_backend.repositories.JobCategoryRepository;
import com.jobconnect_backend.services.IJobCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobCategoryServiceImpl implements IJobCategoryService {
    private final JobCategoryRepository jobCategoryRepository;

    @Override
    public List<JobCategoryResponse> getAllJobCategories() {
        List<JobCategory> jobCategories = jobCategoryRepository.findAll();
        return jobCategories.stream()
                .map(jobCategory -> JobCategoryResponse.builder()
                        .jobCategoryId(jobCategory.getJobCategoryId())
                        .name(jobCategory.getName())
                        .build())
                .toList();
    }

    @Override
    public void addJobCategory(String jobCategoryName) {

    }

    @Override
    public void deleteJobCategory(Integer jobCategoryId) {

    }

    @Override
    public void updateJobCategory(Integer jobCategoryId, String jobCategoryName) {

    }
}
