package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.JobSeekerProfileConverter;
import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.repositories.CompanyRepository;
import com.jobconnect_backend.repositories.JobSeekerProfileRepository;
import com.jobconnect_backend.repositories.SavedJobSeekerRepository;
import com.jobconnect_backend.services.ISavedJobSeekerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedJobSeekerServiceImpl implements ISavedJobSeekerService {
    private final SavedJobSeekerRepository savedJobSeekerRepository;
    private final JobSeekerProfileRepository jobSeekerRepository;
    private final CompanyRepository companyRepository;
    private final JobSeekerProfileConverter jobSeekerProfileConverter;

    @Override
    public List<JobSeekerProfileDTO> getListSavedJobSeekers(Integer companyId) {
        return List.of();
    }
}
