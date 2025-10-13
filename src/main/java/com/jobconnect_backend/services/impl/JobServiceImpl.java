package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.JobConverter;
import com.jobconnect_backend.dto.dto.JobDTO;
import com.jobconnect_backend.dto.request.CreateJobRequest;
import com.jobconnect_backend.dto.request.UpdateJobRequest;
import com.jobconnect_backend.entities.Company;
import com.jobconnect_backend.entities.Job;
import com.jobconnect_backend.entities.JobCategory;
import com.jobconnect_backend.entities.Skill;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.*;
import com.jobconnect_backend.services.IJobService;
import com.jobconnect_backend.utils.ValidateField;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @Override
    public void createJob(CreateJobRequest request, BindingResult bindingResult) {
        Map<String, String> errors = validateField.getErrors(bindingResult);

        if (!errors.isEmpty()) {
            throw new BadRequestException("Please complete all required fields to proceed.", errors);
        }

        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new BadRequestException("Company not found"));
        if(company.getCreateJobCount() <= 0){
            throw new BadRequestException("You have reached the maximum number of job postings");
        }

        company.setCreateJobCount(company.getCreateJobCount() - 1);

        if(!company.getIsVerified()){
            throw new BadRequestException("Company must be verified to post a job");
        }

        List<Skill> skills = skillRepository.findAllById(request.getSkillIds());
        if (skills.size() != request.getSkillIds().size()) {
            throw new BadRequestException("Some skills are invalid");
        }

        List<JobCategory> categories = jobCategoryRepository.findAllById(request.getCategoryIds());
        if (categories.size() != request.getCategoryIds().size()) {
            throw new BadRequestException("Some categories are invalid");
        }

        Job job = Job.builder()
                .company(company)
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .benefits(request.getBenefits())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .yearsOfExperience( request.getYearsOfExperience())
                .educationLevel(request.getEducationLevel())
                .jobType(request.getJobType())
                .location(request.getLocation())
                .postedAt(LocalDateTime.now())
                .deadline(request.getDeadline())
                .isActive(true)
                .isExpired(false)
                .isPending(false)
                .isDeleted(false)
                .isApproved(false)
                .skills(skills)
                .categories(categories)
                .build();

        if(company.getUser().isVip() && company.getUser().getVipExpiryDate().isAfter(LocalDateTime.now())){
            if(company.getUser().getVipLevel() == 1){
                job.setIsPriority(true);
                job.setPriorityLevel(1);
            } else if(company.getUser().getVipLevel() == 2){
                job.setIsPriority(true);
                job.setPriorityLevel(2);
                job.setIsActive(true);
            }
        } else {
            job.setIsPriority(false);
            job.setPriorityLevel(0);
            job.setIsApproved(false);
        }
        jobRepository.save(job);
    }

    @Override
    public void updateJob(UpdateJobRequest request, BindingResult bindingResult) {
        Map<String, String> errors = validateField.getErrors(bindingResult);

        if (!errors.isEmpty()) {
            throw new BadRequestException("Please complete all required fields to proceed.", errors);
        }

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new BadRequestException("Job not found"));

        List<Skill> skills = skillRepository.findAllById(request.getSkillIds());
        if (skills.size() != request.getSkillIds().size()) {
            throw new BadRequestException("Some skills are invalid");
        }

        List<JobCategory> categories = jobCategoryRepository.findAllById(request.getCategoryIds());
        if (categories.size() != request.getCategoryIds().size()) {
            throw new BadRequestException("Some categories are invalid");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setBenefits(request.getBenefits());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setJobType(request.getJobType());
        job.setEducationLevel(request.getEducationLevel());
        job.setYearsOfExperience( request.getYearsOfExperience());
        job.setLocation(request.getLocation());
        job.setDeadline(request.getDeadline());
        job.setIsActive(request.getIsActive());
        job.setIsPending(true);
        job.setSkills(skills);
        job.setCategories(categories);
        job.setIsApproved(false);

        jobRepository.save(job);
    }

    @Override
    public void deleteJob(Integer jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BadRequestException("Job not found"));

        job.setIsDeleted(true);
        jobRepository.save(job);
    }
}
