package com.jobconnect_backend.converters;

import com.jobconnect_backend.dto.dto.WorkExperienceDTO;
import com.jobconnect_backend.entities.WorkExperience;
import com.jobconnect_backend.populators.WorkExperiencePopulator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class WorkExperienceConverter {
    private final WorkExperiencePopulator workExperiencePopulator;

    public WorkExperienceDTO convertToWorkExperienceDTO(WorkExperience workExperience) {
        WorkExperienceDTO dto = new WorkExperienceDTO();
        workExperiencePopulator.populate(workExperience, dto);
        return dto;
    }
}
