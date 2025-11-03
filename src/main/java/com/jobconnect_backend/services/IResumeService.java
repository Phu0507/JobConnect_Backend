package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.request.ResumeRequest;
import org.springframework.validation.BindingResult;

import java.io.IOException;

public interface IResumeService {
    void createResume(Integer profileId, ResumeRequest request, BindingResult result) throws IOException;
    void deleteResume(Integer resumeId);
    void autoCreateResume(Integer profileId, String resumeName);
}
