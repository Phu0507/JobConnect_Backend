package com.jobconnect_backend.services.impl;

import com.algolia.api.SearchClient;
import com.algolia.config.ClientOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jobconnect_backend.converters.JobConverter;
import com.jobconnect_backend.dto.dto.JobDTO;
import com.jobconnect_backend.dto.request.CreateJobRequest;
import com.jobconnect_backend.dto.request.RejectJobRequest;
import com.jobconnect_backend.dto.request.UpdateJobRequest;
import com.jobconnect_backend.entities.*;
import com.jobconnect_backend.entities.enums.Role;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.*;
import com.jobconnect_backend.services.IJobService;
import com.jobconnect_backend.utils.ValidateField;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.*;

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

    @Override
    public void approveJob(Integer jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BadRequestException("Job not found"));

        job.setIsApproved(true);
        job.setIsPending(false);
        jobRepository.save(job);
    }

    @Override
    public void rejectJob(RejectJobRequest request) {
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new BadRequestException("Job not found"));

        job.setIsApproved(false);
        job.setNote(request.getReason());
        job.setIsPending(false);
        jobRepository.save(job);
    }

    @Override
    public JobDTO getJobByID(Integer jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() ->
                new BadRequestException("Job not found"));

//        if(job.getIsDeleted() || !job.getIsActive() || !job.getIsApproved()){
//            throw new BadRequestException("Job is not available");
//        }

        return jobConverter.convertToJobDTO(job);
    }

    @Override
    public List<JobDTO> searchJobs(String keyword, List<String> locations, List<Integer> jobCategoryIds) {
        boolean isKeywordEmpty = keyword == null || keyword.trim().isEmpty();
        boolean isLocationsEmpty = locations == null || locations.isEmpty();
        boolean isCategoryIdsEmpty = jobCategoryIds == null || jobCategoryIds.isEmpty();

        List<Job> jobs;

        if (isKeywordEmpty && isLocationsEmpty && isCategoryIdsEmpty) {
            jobs = jobRepository.findAll().stream()
                    .filter(job -> job.getIsActive() && !job.getIsDeleted() && job.getIsApproved() && !job.getIsExpired())
                    .toList();
        } else {
            jobs = jobRepository.searchJobs(keyword, locations, jobCategoryIds);
            jobs = jobs.stream()
                    .filter(job -> job.getIsActive() && !job.getIsDeleted() && job.getIsApproved() && !job.getIsExpired())
                    .toList();
        }

        return jobs.stream().map(jobConverter::convertToJobDTO).toList();
    }

    @Override
    public List<JobDTO> getJobsByCompanyId(Integer companyId, Integer id) {
        List<Job> jobs;

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Company not found"));

        boolean isCompanyOwner = company.getUser().getRole().equals(Role.COMPANY) &&
                id.equals(companyId);

        if (isCompanyOwner) {
            jobs = jobRepository.findByCompanyCompanyId(companyId);
        } else {
            jobs = jobRepository.findByCompanyCompanyId(companyId)
                    .stream()
                    .filter(job -> job.getIsActive() && !job.getIsDeleted() && job.getIsApproved() && !job.getIsExpired())
                    .toList();
        }

        return jobs.stream().map(jobConverter::convertToJobDTO).toList();
    }

    @Override
    public Map<String, List<Integer>> getSkillsAndCategories(Integer jobSeekerId) {
        JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(jobSeekerId)
                .orElseThrow(() -> new BadRequestException("JobSeeker not found"));

        List<Integer> skillIds = new ArrayList<>();
        if (jobSeekerProfile.getSkills() != null) {
            skillIds.addAll(jobSeekerProfile.getSkills().stream()
                    .map(Skill::getSkillId)
                    .toList());
        }
        if (jobSeekerProfile.getWorkExperiences() != null) {
            jobSeekerProfile.getWorkExperiences().forEach(workExperience -> {
                if (workExperience.getSkills() != null) {
                    workExperience.getSkills().forEach(skill -> {
                        if (!skillIds.contains(skill.getSkillId())) {
                            skillIds.add(skill.getSkillId());
                        }
                    });
                }
            });
        }

        List<Integer> categoryIds = new ArrayList<>();
        if (jobSeekerProfile.getWorkExperiences() != null) {
            jobSeekerProfile.getWorkExperiences().forEach(workExperience -> {
                if (workExperience.getCategories() != null) {
                    workExperience.getCategories().forEach(category -> {
                        if (!categoryIds.contains(category.getJobCategoryId())) {
                            categoryIds.add(category.getJobCategoryId());
                        }
                    });
                }
            });
        }

        Map<String, List<Integer>> result = new HashMap<>();
        result.put("skillIds", skillIds);
        result.put("categoryIds", categoryIds);
        return result;
    }

    @Override
    public List<JobDTO> getJobsByCategory(Integer categoryId) {
        List<Job> list = jobRepository.findByCategoryId(categoryId);
        return list.stream().map(jobConverter::convertToJobDTO).toList();
    }

    @Override
    public List<JobPosition> getAllJobPosition() {
        List<JobPosition> jobPositions = jobPositionRepository.findAll();
        if (jobPositions.isEmpty()) {
            throw new BadRequestException("No job positions found");
        }
        return jobPositions;
    }

    @Override
    public List<JobDTO> getJobsPriority() {
        List<Job> jobs = jobRepository.findJobsWithPriorityLevel2();
        if (jobs.isEmpty()) {
            return Collections.emptyList();
        }
        return jobs.stream()
                .map(jobConverter::convertToJobDTO)
                .toList();
    }

    @Override
    public void pushJobsToAlgolia() throws IOException {
        SearchClient client = new SearchClient(algoliaID, algoliaKey, ClientOptions.builder().build());

        try {
            List<JobDTO> jobs = getAllJobs();

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            List<Map> jobData = jobs.stream()
                    .map(job -> mapper.convertValue(job, Map.class))
                    .toList();

            String indexName = "jobs_index";
            client.saveObjects(indexName, jobData);

            System.out.println("Push " + jobData.size() + " jobs to Algolia index: " + indexName);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    @Override
    public List<JobDTO> getProposedJobs(Integer jobSeekerId) {
        JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(jobSeekerId)
                .orElseThrow(() -> new BadRequestException("JobSeeker not found"));

        List<Integer> skillIds = new ArrayList<>();

        if (jobSeekerProfile.getSkills() != null) {
            skillIds.addAll(jobSeekerProfile.getSkills().stream()
                    .map(Skill::getSkillId)
                    .toList());
        }

        if (jobSeekerProfile.getWorkExperiences() != null) {
            jobSeekerProfile.getWorkExperiences().forEach(workExperience -> {
                if (workExperience.getSkills() != null) {
                    workExperience.getSkills().forEach(skill -> {
                        if (!skillIds.contains(skill.getSkillId())) {
                            skillIds.add(skill.getSkillId());
                        }
                    });
                }
            });
        }

        List<Integer> categoryIds = new ArrayList<>();
        if (jobSeekerProfile.getWorkExperiences() != null) {
            jobSeekerProfile.getWorkExperiences().forEach(workExperience -> {
                if (workExperience.getCategories() != null) {
                    workExperience.getCategories().forEach(category -> {
                        if (!categoryIds.contains(category.getJobCategoryId())) {
                            categoryIds.add(category.getJobCategoryId());
                        }
                    });
                }
            });
        }

        if (skillIds.isEmpty() && categoryIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Job> proposedJobs = jobRepository.findProposedJobs(skillIds, categoryIds);

        return proposedJobs.stream()
                .map(jobConverter::convertToJobDTO)
                .toList();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAndUpdateExpiredJobs() {
        List<Job> activeJobs = jobRepository.findByIsActiveTrueAndIsExpiredFalseAndIsDeletedFalse();
        LocalDateTime now = LocalDateTime.now();

        for (Job job : activeJobs) {
            if (job.getDeadline() != null && job.getDeadline().isBefore(ChronoLocalDate.from(now))) {
                job.setIsExpired(true);
                job.setIsActive(false);
                jobRepository.save(job);
            }
        }
    }
}
