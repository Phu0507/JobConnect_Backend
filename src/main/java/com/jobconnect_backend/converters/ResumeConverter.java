package com.jobconnect_backend.converters;

import com.jobconnect_backend.dto.dto.ResumeDTO;
import com.jobconnect_backend.entities.Resume;
import com.jobconnect_backend.populators.ResumePopulator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ResumeConverter {
    private final ResumePopulator resumePopulator;

    public ResumeDTO convertToResumeDTO(Resume resume) {
        ResumeDTO dto = new ResumeDTO();
        resumePopulator.populate(resume, dto);
        return dto;
    }
}