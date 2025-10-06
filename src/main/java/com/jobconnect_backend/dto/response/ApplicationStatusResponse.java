package com.jobconnect_backend.dto.response;

import com.jobconnect_backend.dto.dto.ApplicationStatusDTO;
import com.jobconnect_backend.dto.dto.JobDTO;
import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.dto.dto.ResumeDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusResponse {
    private Integer applicationId;
    private JobDTO job;
    private JobSeekerProfileDTO jobSeekerProfile;
    private ResumeDTO resumeApplied;
    private List<ApplicationStatusDTO> statusDTOList;
}
