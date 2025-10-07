package com.jobconnect_backend.converters;

import com.jobconnect_backend.dto.dto.JobDTO;
import com.jobconnect_backend.entities.Job;
import com.jobconnect_backend.populators.JobPopulator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JobConverter {
    private final JobPopulator jobPopulator;

    public JobDTO convertToJobDTO(Job job) {
        JobDTO dto = new JobDTO();
        jobPopulator.populate(job, dto);
        return dto;
    }
}
