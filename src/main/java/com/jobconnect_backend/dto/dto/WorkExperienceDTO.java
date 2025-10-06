package com.jobconnect_backend.dto.dto;

import com.jobconnect_backend.entities.JobCategory;
import com.jobfind.models.enums.JobType;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperienceDTO {
    private String jobTitle;
    private Integer id;
    private Integer jobPositionId;
    private Integer companyId;
    private String companyName;
    private String logo;
    private String description;
    private JobType jobType;
    private String startDate;
    private String endDate;
    private List<SkillDTO> skills;
    private List<JobCategory> categories;
}