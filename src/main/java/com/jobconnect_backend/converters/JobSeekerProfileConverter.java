package com.jobconnect_backend.converters;

import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.entities.JobSeekerProfile;
import com.jobconnect_backend.populators.JobSeekerProfilePopulator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JobSeekerProfileConverter {
    private final JobSeekerProfilePopulator jobSeekerProfilePopulator;

    public JobSeekerProfileDTO convertToJobSeekerProfileDTO(JobSeekerProfile jobSeekerProfile) {
        JobSeekerProfileDTO dto = new JobSeekerProfileDTO();
        jobSeekerProfilePopulator.populate(jobSeekerProfile, dto);
        return dto;
    }
}
