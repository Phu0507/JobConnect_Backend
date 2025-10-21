package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;

import java.util.List;

public interface ISavedJobSeekerService {
    List<JobSeekerProfileDTO> getListSavedJobSeekers(Integer companyId);
}
