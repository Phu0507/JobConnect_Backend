package com.jobconnect_backend.dto.dto;


import com.jobconnect_backend.entities.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationStatusDTO {
    private ApplicationStatus status;
    private LocalDateTime time;
}
