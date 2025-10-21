package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.dto.response.SavedJobResponse;
import com.jobconnect_backend.entities.Job;
import com.jobconnect_backend.entities.JobSeekerProfile;
import com.jobconnect_backend.entities.SavedJob;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.JobRepository;
import com.jobconnect_backend.repositories.JobSeekerProfileRepository;
import com.jobconnect_backend.repositories.SavedJobRepository;
import com.jobconnect_backend.services.ISavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedJobServiceImpl implements ISavedJobService {
    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;

    @Override
    public List<SavedJobResponse> getListSavedJobs(Integer jobSeekerProfileId) {
        List<SavedJob> list = savedJobRepository.findByJobSeekerProfileProfileId(jobSeekerProfileId);

        return list.stream()
                .map(savedJob -> SavedJobResponse.builder()
                        .jobId(savedJob.getJob().getJobId())
                        .companyId(savedJob.getJob().getCompany().getCompanyId())
                        .jobName(savedJob.getJob().getTitle())
                        .companyName(savedJob.getJob().getCompany().getCompanyName())
                        .companyLogo(savedJob.getJob().getCompany().getLogoPath())
                        .salaryMin(savedJob.getJob().getSalaryMin())
                        .salaryMax(savedJob.getJob().getSalaryMax())
                        .jobType(savedJob.getJob().getJobType())
                        .location(savedJob.getJob().getLocation())
                        .postedAt(savedJob.getJob().getPostedAt())
                        .deadline(savedJob.getJob().getDeadline())
                        .savedAt(savedJob.getSavedAt())
                        .build())
                .toList();
    }

    @Override
    public void saveJob(Integer jobId, Integer jobSeekerProfileId) {
        if (savedJobRepository.findByJobJobIdAndJobSeekerProfileProfileId(jobId, jobSeekerProfileId).isPresent()) {
            throw new BadRequestException("Job already saved");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BadRequestException("Job not found"));

        JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(jobSeekerProfileId)
                .orElseThrow(() -> new BadRequestException("JobSeekerProfile not found"));

        SavedJob savedJob = SavedJob.builder()
                .job(job)
                .jobSeekerProfile(jobSeekerProfile)
                .savedAt(LocalDate.now())
                .build();

        savedJobRepository.save(savedJob);
    }

    @Override
    public void unsaveJob(Integer jobId, Integer jobSeekerProfileId) {
        SavedJob savedJob = savedJobRepository.findByJobJobIdAndJobSeekerProfileProfileId(jobId, jobSeekerProfileId)
                .orElseThrow(() -> new BadRequestException("Saved job not found"));

        savedJobRepository.delete(savedJob);
    }
}
