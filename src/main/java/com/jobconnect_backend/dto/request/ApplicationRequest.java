package com.jobconnect_backend.dto.request;

import lombok.Data;

@Data
public class ApplicationRequest {
    private Integer jobId;
    private Integer jobSeekerProfileId;
    private Integer resumeId;
}
