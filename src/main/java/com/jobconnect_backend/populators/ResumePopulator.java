package com.jobconnect_backend.populators;

import com.jobconnect_backend.dto.dto.ResumeDTO;
import com.jobconnect_backend.entities.Resume;
import org.springframework.stereotype.Component;

@Component
public class ResumePopulator {
    public void populate(Resume source, ResumeDTO target) {
        target.setResumeId(source.getResumeId());
        target.setResumeName(source.getResumeName());
        target.setResumePath(source.getResumePath());
        target.setUploadedAt(source.getUploadedAt());
        target.setDeleted(source.isDeleted());
    }
}
