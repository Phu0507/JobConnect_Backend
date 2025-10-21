package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;

import java.util.List;

public interface ISavedJobSeekerService {
    void saveJobSeeker(Integer jobSeekerProfileId, Integer companyId);
    void unsaveJobSeeker(Integer jobSeekerProfileId, Integer companyId);
    List<JobSeekerProfileDTO> getListSavedJobSeekers(Integer companyId);
}
