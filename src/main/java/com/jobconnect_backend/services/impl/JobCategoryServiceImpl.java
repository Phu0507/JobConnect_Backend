package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.dto.response.JobCategoryResponse;
import com.jobconnect_backend.entities.JobCategory;
import com.jobconnect_backend.exception.BadRequestException;
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
        if(jobCategoryRepository.existsByName(jobCategoryName)){
            throw new BadRequestException("Job Category already exists");
        }
        JobCategory jobCategory = JobCategory.builder()
                .name(jobCategoryName)
                .build();
        jobCategoryRepository.save(jobCategory);
    }

    @Override
    public void deleteJobCategory(Integer jobCategoryId) {
        jobCategoryRepository.deleteById(jobCategoryId);
    }

    @Override
    public void updateJobCategory(Integer jobCategoryId, String jobCategoryName) {
        JobCategory jobCategory = jobCategoryRepository.findById(jobCategoryId).orElseThrow(
                () -> new BadRequestException("Job Category not found"));
        jobCategory.setName(jobCategoryName);
        jobCategoryRepository.save(jobCategory);
    }
}
