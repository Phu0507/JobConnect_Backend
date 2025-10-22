package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.request.ApplicationRequest;
import com.jobconnect_backend.dto.response.ApplicationStatusResponse;

import java.util.List;

public interface IApplicationService {
    List<ApplicationStatusResponse> getAllApplications();
    void applyForJob(ApplicationRequest request);
}
