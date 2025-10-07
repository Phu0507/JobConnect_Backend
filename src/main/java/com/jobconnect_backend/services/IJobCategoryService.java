package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.response.JobCategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IJobCategoryService {
    List<JobCategoryResponse> getAllJobCategories();
    void addJobCategory(String jobCategoryName);
    void deleteJobCategory(Integer jobCategoryId);
    void updateJobCategory(Integer jobCategoryId, String jobCategoryName);
}
