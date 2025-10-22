package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.JobConverter;
import com.jobconnect_backend.converters.JobSeekerProfileConverter;
import com.jobconnect_backend.converters.ResumeConverter;
import com.jobconnect_backend.dto.dto.ApplicationStatusDTO;
import com.jobconnect_backend.dto.request.ApplicationRequest;
import com.jobconnect_backend.dto.response.ApplicationOfJobResponse;
import com.jobconnect_backend.dto.response.ApplicationStatusResponse;
import com.jobconnect_backend.entities.*;
import com.jobconnect_backend.entities.enums.ApplicationStatus;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.*;
import com.jobconnect_backend.services.IApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements IApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final ResumeRepository resumeRepository;
    //    private final NotificationServiceImplService notificationServiceImpl;
    private final JobSeekerProfileConverter jobSeekerProfileConverter;
    private final ResumeConverter resumeConverter;
    private final JobConverter jobConverter;
    private final SimpMessagingTemplate messagingTemplate;

    // lấy danh sách tất cả các đơn ứng tuyển
    @Override
    public List<ApplicationStatusResponse> getAllApplications() {
        List<Application> applications = applicationRepository.findAll();
        if (applications.isEmpty()) {
            throw new BadRequestException("No applications found");
        }

        return getApplicationStatusResponses(applications);
    }

    private List<ApplicationStatusResponse> getApplicationStatusResponses(List<Application> applications) {
        return applications.stream()
                .map(application -> ApplicationStatusResponse.builder()
                        .applicationId(application.getApplicationId())
                        .job(jobConverter.convertToJobDTO(application.getJob()))
                        .jobSeekerProfile(jobSeekerProfileConverter.convertToJobSeekerProfileDTO(application.getJobSeekerProfile()))
                        .resumeApplied(resumeConverter.convertToResumeDTO(application.getResume()))
                        .statusDTOList(
                                historyRepository.findByApplicationApplicationId(application.getApplicationId()).stream()
                                        .map(history -> ApplicationStatusDTO.builder()
                                                .status(history.getApplicationStatus())
                                                .time(history.getTime())
                                                .build())
                                        .collect(Collectors.toList())
                        )
                        .build())
                .collect(Collectors.toList());
    }

    // Kiểm tra công việc, hồ sơ, CV hợp lệ và chưa nộp trước đó, sau đó tạo mới một đơn ứng tuyển (Application)
    // và lưu lịch sử trạng thái ban đầu (PENDING)
    @Override
    public void applyForJob(ApplicationRequest request) {
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new BadRequestException("Job not found"));

        if (!job.getIsActive() || job.getIsDeleted() || !job.getIsApproved()) {
            throw new BadRequestException("Job is not available for application");
        }

        JobSeekerProfile jobSeeker = jobSeekerProfileRepository.findById(request.getJobSeekerProfileId())
                .orElseThrow(() -> new BadRequestException("Job seeker profile not found"));

        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new BadRequestException("Resume not found"));

        List<Application> existingApplications = applicationRepository.findByJobJobIdAndJobSeekerProfileProfileId(
                request.getJobId(), request.getJobSeekerProfileId()
        );

        if (!existingApplications.isEmpty()) {
            throw new BadRequestException("Application already exists");
        }

        Application application = Application.builder()
                .job(job)
                .jobSeekerProfile(jobSeeker)
                .resume(resume)
                .appliedAt(LocalDateTime.now())
                .applicationStatus(ApplicationStatus.PENDING)
                .build();
        applicationRepository.save(application);
        saveApplicationStatusHistory(application, ApplicationStatus.PENDING);
    }

    private void saveApplicationStatusHistory(Application application, ApplicationStatus status) {
        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .application(application)
                .applicationStatus(status)
                .time(LocalDateTime.now())
                .build();
        historyRepository.save(history);
    }

    // Lấy danh sách tất cả các đơn ứng tuyển (Application) của một công việc cụ thể theo job id
    @Override
    public List<ApplicationOfJobResponse> getApplicationOfJob(Integer jobId) {
        List<Application> applications = applicationRepository.findByJobJobId(jobId);

        if(applications.isEmpty()){
            throw new BadRequestException("No applications found for the given job ID");
        }

        return applications.stream()
                .map(application -> ApplicationOfJobResponse.builder()
                        .applicationId(application.getApplicationId())
                        .JobSeekerProfileDTO(jobSeekerProfileConverter.convertToJobSeekerProfileDTO(application.getJobSeekerProfile()))
                        .appliedAt(application.getAppliedAt())
                        .status(application.getApplicationStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationStatusResponse> getApplicationOfJobByJobSeeker(Integer jobSeekerId) {
        List<Application> applications = applicationRepository.findByJobSeekerProfileProfileId(jobSeekerId);
        if(applications.isEmpty()){
            throw new BadRequestException("No applications found for the given job seeker ID");
        }
        return getApplicationStatusResponses(applications);
    }

}
