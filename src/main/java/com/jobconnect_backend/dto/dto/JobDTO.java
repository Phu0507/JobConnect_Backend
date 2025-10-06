package com.jobconnect_backend.dto.dto;

import com.jobconnect_backend.entities.JobCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@NoArgsConstructor
@Data
public class JobDTO {
    private Integer jobId;
    private CompanyDTO company;
    private String title;
    private String description;
    private String requirements;
    private String benefits;
    private Double salaryMin;
    private Double salaryMax;
    private String jobType;
    private String location;
    private String yearsOfExperience;
    private String educationLevel;
    private LocalDateTime postedAt;
    private LocalDate deadline;
    private Boolean isActive;
    private Boolean isApproved;
    private boolean isExpired;
    private Boolean isPending;
    private Boolean isDeleted;
    private Boolean isPriority;
    private Integer priorityLevel;
    private List<SkillDTO> skills;
    private List<JobCategory> categories;
    private String note;
}
