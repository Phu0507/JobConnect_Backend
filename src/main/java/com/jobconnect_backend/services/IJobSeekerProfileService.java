package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.dto.response.JobSeekerProfileResponse;

import java.util.List;

public interface IJobSeekerProfileService {
    List<JobSeekerProfileDTO> getAllJobSeekerProfiles();
    JobSeekerProfileResponse getProfileByUserId(Integer userId);
}
