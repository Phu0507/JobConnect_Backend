package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.response.SavedJobResponse;

import java.util.List;

public interface ISavedJobService {
    List<SavedJobResponse> getListSavedJobs(Integer jobSeekerProfileId);
    void saveJob(Integer jobId, Integer jobSeekerProfileId);
    void unsaveJob(Integer jobId, Integer jobSeekerProfileId);
}
