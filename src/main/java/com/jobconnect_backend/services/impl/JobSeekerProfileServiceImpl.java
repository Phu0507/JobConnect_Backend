package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.JobSeekerProfileConverter;
import com.jobconnect_backend.converters.ResumeConverter;
import com.jobconnect_backend.converters.SkillConverter;
import com.jobconnect_backend.converters.WorkExperienceConverter;
import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
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
}
