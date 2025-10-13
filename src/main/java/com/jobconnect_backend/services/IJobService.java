package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.JobDTO;
import com.jobconnect_backend.dto.request.CreateJobRequest;
import com.jobconnect_backend.dto.request.RejectJobRequest;
import com.jobconnect_backend.dto.request.UpdateJobRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

@Service
public interface IJobService {
    List<JobDTO> getAllJobs();
    void createJob(CreateJobRequest request, BindingResult bindingResult);
    void updateJob(UpdateJobRequest request, BindingResult bindingResult);
    void deleteJob(Integer jobId);
    void approveJob(Integer jobId);
    void rejectJob(RejectJobRequest request);
    JobDTO getJobByID(Integer jobId);
    List<JobDTO> searchJobs(String keyword, List<String> locations, List<Integer> jobCategoryIds);
    List<JobDTO> getJobsByCompanyId(Integer companyId, Integer id);
    Map<String, List<Integer>> getSkillsAndCategories(Integer jobSeekerId);
//    List<JobDTO> getJobsByCategory(Integer categoryId);
}
