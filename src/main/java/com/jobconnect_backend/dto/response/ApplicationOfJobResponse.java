package com.jobconnect_backend.dto.response;

import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.entities.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationOfJobResponse {
    private Integer applicationId;
    private JobSeekerProfileDTO JobSeekerProfileDTO;
    private LocalDateTime appliedAt;
    private ApplicationStatus status;
}
