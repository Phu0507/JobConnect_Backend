package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.JobSeekerProfileConverter;
import com.jobconnect_backend.converters.ResumeConverter;
import com.jobconnect_backend.converters.SkillConverter;
import com.jobconnect_backend.converters.WorkExperienceConverter;
import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.dto.dto.WorkExperienceDTO;
import com.jobconnect_backend.dto.request.CreateWorkExperienceRequest;
import com.jobconnect_backend.dto.request.SkillRequest;
import com.jobconnect_backend.dto.request.UpdateWorkExperienceRequest;
import com.jobconnect_backend.dto.response.JobSeekerProfileResponse;
import com.jobconnect_backend.entities.JobCategory;
import com.jobconnect_backend.entities.JobSeekerProfile;
import com.jobconnect_backend.entities.Skill;
import com.jobconnect_backend.entities.WorkExperience;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.*;
import com.jobconnect_backend.services.IJobSeekerProfileService;
import com.jobconnect_backend.utils.ValidateField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSeekerProfileServiceImpl implements IJobSeekerProfileService {
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final CompanyRepository companyRepository;
    private final JobPositionRepository jobPositionRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final SkillRepository skillRepository;
    private final SkillConverter skillConverter;
    private final WorkExperienceConverter workExperienceConverter;
    private final JobSeekerProfileConverter jobSeekerProfileConverter;
    private final ResumeConverter resumeConverter;
    private final ValidateField validateField;

    public JobSeekerProfile getJobSeekerProfile(Integer userId) {
        return jobSeekerProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BadRequestException("JobSeekerProfile not found"));
    }

    private List<Skill> getSkillsByIds(List<Integer> skillIds) {
        return skillIds.stream()
                .map(skillId -> skillRepository.findById(skillId)
                        .orElseThrow(() -> new BadRequestException("Skill not found")))
                .collect(Collectors.toList());
    }

    private List<JobCategory> getJobCategoriesByIds(List<Integer> categoryIds) {
        return categoryIds.stream()
                .map(categoryId -> jobCategoryRepository.findById(categoryId)
                        .orElseThrow(() -> new BadRequestException("Job Category not found")))
                .collect(Collectors.toList());
    }

    @Override
    public List<JobSeekerProfileDTO> getAllJobSeekerProfiles() {
        List<JobSeekerProfile> jobSeekerProfiles = jobSeekerProfileRepository.findAll();
        if (jobSeekerProfiles.isEmpty()) {
            return new ArrayList<>();
        }
        return jobSeekerProfiles.stream()
                .map(jobSeekerProfileConverter::convertToJobSeekerProfileDTO)
                .collect(Collectors.toList());
    }

    @Override
    public JobSeekerProfileResponse getProfileByUserId(Integer userId) {
        JobSeekerProfile jobSeekerProfile = getJobSeekerProfile(userId);

        List<WorkExperienceDTO> workExperiences = jobSeekerProfile.getWorkExperiences().stream()
                .map(workExperienceConverter::convertToWorkExperienceDTO)
                .collect(Collectors.toList());

        return JobSeekerProfileResponse.builder()
                .resumeList(jobSeekerProfile.getResumes().stream()
                        .filter(resume -> !resume.isDeleted())
                        .map(resumeConverter::convertToResumeDTO)
                        .collect(Collectors.toList()))
                .workExperiences(workExperiences)
                .avatar(jobSeekerProfile.getAvatar())
                .title(jobSeekerProfile.getTitle())
                .skills(jobSeekerProfile.getSkills().stream()
                        .map(skillConverter::convertToSkillDTO)
                        .collect(Collectors.toList()))
                .firstName(jobSeekerProfile.getFirstName())
                .address(jobSeekerProfile.getAddress())
                .birthDay(jobSeekerProfile.getUser().getJobSeekerProfile().getBirthDay())
                .lastName(jobSeekerProfile.getLastName())
                .email(jobSeekerProfile.getUser().getEmail())
                .phone(jobSeekerProfile.getUser().getPhone())
                .build();
    }

    @Override
    public JobSeekerProfileDTO getProfileById(Integer jobSeekerId) {
        JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(jobSeekerId)
                .orElseThrow(() -> new BadRequestException("JobSeekerProfile not found"));
        return jobSeekerProfileConverter.convertToJobSeekerProfileDTO(jobSeekerProfile);
    }

    @Override
    public void addWorkExperience(Integer jobSeekerId, CreateWorkExperienceRequest request, BindingResult result) {
        JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(jobSeekerId)
                .orElseThrow(() -> new BadRequestException("JobSeekerProfile not found"));

        Map<String, String> errors = validateField.getErrors(result);
        if (!errors.isEmpty()) {
            throw new BadRequestException("Please complete all required fields to proceed.", errors);
        }

        boolean exists = workExperienceRepository.existsByJobSeekerProfileAndCompanyAndJobPosition(
                jobSeekerProfile,
                companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new BadRequestException("Company not found")),
                jobPositionRepository.findById(request.getJobPositionId()).orElseThrow(() -> new BadRequestException("Job position not found"))
        );

        if (exists) {
            throw new BadRequestException("Work experience already exists.");
        }

        WorkExperience workExperience = WorkExperience.builder()
                .jobType(request.getJobType())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .company(companyRepository.findById(request.getCompanyId())
                        .orElseThrow(() -> new BadRequestException("Company not found")))
                .jobPosition(jobPositionRepository.findById(request.getJobPositionId())
                        .orElseThrow(() -> new BadRequestException("Job position not found")))
                .skills(getSkillsByIds(request.getSkills()))
                .categories(getJobCategoriesByIds(request.getCategories()))
                .jobSeekerProfile(jobSeekerProfile)
                .build();

        workExperienceRepository.save(workExperience);
    }

    @Override
    public void updateWorkExperience(Integer jobSeekerId, UpdateWorkExperienceRequest request, BindingResult result) {
        JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(jobSeekerId)
                .orElseThrow(() -> new BadRequestException("JobSeekerProfile not found"));

        Map<String, String> errors = validateField.getErrors(result);
        if (!errors.isEmpty()) {
            throw new BadRequestException("Please complete all required fields to proceed.", errors);
        }

        WorkExperience workExperience = workExperienceRepository.findById(request.getWorkExperienceId())
                .orElseThrow(() -> new BadRequestException("WorkExperience not found with ID: " + request.getWorkExperienceId()));

        workExperience.setJobType(request.getJobType());
        workExperience.setDescription(request.getDescription());
        workExperience.setStartDate(request.getStartDate());
        workExperience.setEndDate(request.getEndDate());

        workExperience.setCompany(companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new BadRequestException("Company not found")));

        workExperience.setJobPosition(jobPositionRepository.findById(request.getJobPositionId())
                .orElseThrow(() -> new BadRequestException("Job position not found")));

        workExperience.setSkills(getSkillsByIds(request.getSkills()));
        workExperience.setCategories(getJobCategoriesByIds(request.getCategories()));
        workExperience.setJobSeekerProfile(jobSeekerProfile);

        workExperienceRepository.save(workExperience);
    }

    @Override
    public void deleteWorkExperience(Integer jobSeekerId, Integer workExperienceId) {
        boolean exists = workExperienceRepository.existsByJobSeekerProfileProfileId(
                jobSeekerId);
        if (!exists) {
            throw new BadRequestException("Work experience not found with ID: " + workExperienceId);
        } else {
            workExperienceRepository.deleteById(workExperienceId);
        }
    }

    @Override
    public void addSkills(SkillRequest createSkillsRequest, BindingResult result) {
        Map<String, String> errors = validateField.getErrors(result);
        if (!errors.isEmpty()) {
            throw new BadRequestException("Please complete all required fields to proceed.", errors);
        }

        JobSeekerProfile profile = getJobSeekerProfile(createSkillsRequest.getProfileId());
        List<Skill> existingSkills = profile.getSkills();
        List<Skill> newSkills = getSkillsByIds(createSkillsRequest.getSkills());

        for (Skill skill : newSkills) {
            if (!existingSkills.contains(skill)) {
                existingSkills.add(skill);
            }
        }

        jobSeekerProfileRepository.save(profile);
    }

    @Override
    public void updateSkills(SkillRequest skillRequest, BindingResult bindingResult) {
        Map<String, String> errors = validateField.getErrors(bindingResult);
        if (!errors.isEmpty()) {
            throw new BadRequestException("Please complete all required fields to proceed.", errors);
        }

        JobSeekerProfile profile = getJobSeekerProfile(skillRequest.getProfileId());

        List<Skill> skills = getSkillsByIds(skillRequest.getSkills());
        profile.setSkills(skills);

        jobSeekerProfileRepository.save(profile);
    }

    @Override
    public List<JobSeekerProfileDTO> searchJobSeekers(String keyword, List<Integer> categoryIds, List<String> locations, Integer companyId) {
        List<JobSeekerProfile> jobSeekers;

        boolean isKeywordEmpty = keyword == null || keyword.trim().isEmpty();
        boolean isCategoryIdsEmpty = categoryIds == null || categoryIds.isEmpty();
        boolean isLocationsEmpty = locations == null || locations.isEmpty();

        if (isKeywordEmpty && isCategoryIdsEmpty && isLocationsEmpty) {
            jobSeekers = jobSeekerProfileRepository.findJobSeekersByCompanyIndustry(companyId);
        } else {
            jobSeekers = jobSeekerProfileRepository.searchJobSeekers(keyword, categoryIds, locations);
        }

        if (jobSeekers.isEmpty()) {
            return new ArrayList<>();
        }

        return jobSeekers.stream()
                .map(jobSeekerProfileConverter::convertToJobSeekerProfileDTO)
                .collect(Collectors.toList());
    }
}
