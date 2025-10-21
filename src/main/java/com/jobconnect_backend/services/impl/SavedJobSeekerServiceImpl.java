package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.JobSeekerProfileConverter;
import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.entities.Company;
import com.jobconnect_backend.entities.JobSeekerProfile;
import com.jobconnect_backend.entities.SavedJobSeeker;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.CompanyRepository;
import com.jobconnect_backend.repositories.JobSeekerProfileRepository;
import com.jobconnect_backend.repositories.SavedJobSeekerRepository;
import com.jobconnect_backend.services.ISavedJobSeekerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobSeekerServiceImpl implements ISavedJobSeekerService {
    private final SavedJobSeekerRepository savedJobSeekerRepository;
    private final JobSeekerProfileRepository jobSeekerRepository;
    private final CompanyRepository companyRepository;
    private final JobSeekerProfileConverter jobSeekerProfileConverter;

    @Override
    public List<JobSeekerProfileDTO> getListSavedJobSeekers(Integer companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new BadRequestException("Company not found"));

        List<SavedJobSeeker> savedJobSeekers = savedJobSeekerRepository.findByCompanyCompanyId(companyId);

        return savedJobSeekers.stream()
                .map(savedJobSeeker -> jobSeekerProfileConverter
                        .convertToJobSeekerProfileDTO(savedJobSeeker.getJobSeekerProfile()))
                .collect(Collectors.toList());
    }

    @Override
    public void saveJobSeeker(Integer jobSeekerProfileId, Integer companyId) {
        JobSeekerProfile jobSeeker = jobSeekerRepository.findById(jobSeekerProfileId)
                .orElseThrow(() -> new BadRequestException("Job Seeker not found"));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new BadRequestException("Company not found"));

        boolean alreadySaved = savedJobSeekerRepository.existsByJobSeekerProfileProfileIdAndCompanyCompanyId(jobSeekerProfileId, companyId);
        if (alreadySaved) {
            throw new BadRequestException("Job Seeker already saved by this company");
        }

        SavedJobSeeker savedJobSeeker = SavedJobSeeker.builder()
                .jobSeekerProfile(jobSeeker)
                .company(company)
                .savedAt(LocalDate.now())
                .build();

        savedJobSeekerRepository.save(savedJobSeeker);
    }

    @Override
    public void unsaveJobSeeker(Integer jobSeekerProfileId, Integer companyId) {
        JobSeekerProfile jobSeeker = jobSeekerRepository.findById(jobSeekerProfileId)
                .orElseThrow(() -> new BadRequestException("Job Seeker not found"));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new BadRequestException("Company not found"));

        SavedJobSeeker savedJobSeeker = savedJobSeekerRepository.findByJobSeekerProfileProfileIdAndCompanyCompanyId(jobSeekerProfileId, companyId)
                .orElseThrow(() -> new BadRequestException("Job Seeker not saved by this company"));

        savedJobSeekerRepository.delete(savedJobSeeker);
    }
}
