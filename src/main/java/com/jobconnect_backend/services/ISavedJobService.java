package com.jobconnect_backend.services;

public interface ISavedJobService {
    void saveJob(Integer jobId, Integer jobSeekerProfileId);
    void unsaveJob(Integer jobId, Integer jobSeekerProfileId);
}
