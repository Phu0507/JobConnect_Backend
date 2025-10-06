package com.jobconnect_backend.dto.response;

import com.jobconnect_backend.dto.dto.ResumeDTO;
import com.jobconnect_backend.dto.dto.SkillDTO;
import com.jobconnect_backend.dto.dto.WorkExperienceDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerProfileResponse {
    private String firstName;
    private String lastName;
    private LocalDate birthDay;
    private String avatar;
    private String email;
    private String phone;
    private String address;
    private String title;
    private List<ResumeDTO> resumeList;
    private List<SkillDTO> skills;
    private List<WorkExperienceDTO> workExperiences;
}