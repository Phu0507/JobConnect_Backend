package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.JobConverter;
import com.jobconnect_backend.converters.JobSeekerProfileConverter;
import com.jobconnect_backend.converters.ResumeConverter;
import com.jobconnect_backend.dto.dto.ApplicationStatusDTO;
import com.jobconnect_backend.dto.response.ApplicationStatusResponse;
import com.jobconnect_backend.entities.Application;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.ApplicationRepository;
import com.jobconnect_backend.repositories.ApplicationStatusHistoryRepository;
import com.jobconnect_backend.repositories.JobRepository;
import com.jobconnect_backend.repositories.JobSeekerProfileRepository;
import com.jobconnect_backend.services.IApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements IApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
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
}
