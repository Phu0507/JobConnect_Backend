package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.dto.request.CreateWorkExperienceRequest;
import com.jobconnect_backend.dto.request.SkillRequest;
import com.jobconnect_backend.dto.request.UpdateWorkExperienceRequest;
import com.jobconnect_backend.dto.response.JobSeekerProfileResponse;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface IJobSeekerProfileService {
    List<JobSeekerProfileDTO> getAllJobSeekerProfiles();
    JobSeekerProfileResponse getProfileByUserId(Integer userId);
    JobSeekerProfileDTO getProfileById(Integer jobSeekerId);
    void addWorkExperience(Integer jobSeekerId, CreateWorkExperienceRequest request, BindingResult result);
    void updateWorkExperience(Integer jobSeekerId, UpdateWorkExperienceRequest request, BindingResult result);
    void deleteWorkExperience(Integer jobSeekerId, Integer workExperienceId);
    void addSkills(SkillRequest createSkillsRequest, BindingResult result);
    void updateSkills(SkillRequest skillRequest, BindingResult bindingResult);
}
