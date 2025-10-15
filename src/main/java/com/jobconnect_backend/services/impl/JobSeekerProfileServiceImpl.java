package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.JobSeekerProfileConverter;
import com.jobconnect_backend.converters.ResumeConverter;
import com.jobconnect_backend.converters.SkillConverter;
import com.jobconnect_backend.converters.WorkExperienceConverter;
import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.dto.dto.WorkExperienceDTO;
import com.jobconnect_backend.dto.response.JobSeekerProfileResponse;
import com.jobconnect_backend.entities.JobCategory;
import com.jobconnect_backend.entities.JobSeekerProfile;
import com.jobconnect_backend.entities.Skill;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.*;
import com.jobconnect_backend.services.IJobSeekerProfileService;
import com.jobconnect_backend.utils.ValidateField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSeekerProfileServiceImpl implements IJobSeekerProfileService {
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
//    private final WorkExperienceRepository workExperienceRepository;
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
}
