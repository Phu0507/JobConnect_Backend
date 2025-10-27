package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.JobConverter;
import com.jobconnect_backend.converters.JobSeekerProfileConverter;
import com.jobconnect_backend.converters.ResumeConverter;
import com.jobconnect_backend.defaults.DefaultValue;
import com.jobconnect_backend.dto.dto.*;
import com.jobconnect_backend.dto.request.ApplicationRequest;
import com.jobconnect_backend.dto.request.CreateNotiRequest;
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
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
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
    private final NotificationServiceImplService notificationServiceImpl;
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

    // lấy lịch sử trạng thái (status history) của một đơn ứng tuyển (application) theo applicationId
    @Override
    public ApplicationStatusResponse getApplicationStatusHistory(Integer applicationId) {
        List<ApplicationStatusHistory> list = historyRepository.findByApplicationApplicationId(applicationId);

        if(list.isEmpty()){
            throw new BadRequestException("No history found for the given application ID");
        }

        return ApplicationStatusResponse.builder()
                .applicationId(applicationId)
                .job(jobConverter.convertToJobDTO(list.get(0).getApplication().getJob()))
                .jobSeekerProfile(jobSeekerProfileConverter.convertToJobSeekerProfileDTO(list.get(0).getApplication().getJobSeekerProfile()))
                .resumeApplied(resumeConverter.convertToResumeDTO(list.get(0).getApplication().getResume()))
                .statusDTOList(
                        list.stream().map(
                                        history -> ApplicationStatusDTO.builder()
                                                .status(history.getApplicationStatus())
                                                .time(history.getTime())
                                                .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public void updateApplicationStatus(Integer applicationId, String status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BadRequestException("Application not found"));

        ApplicationStatus newStatus = ApplicationStatus.valueOf(status);
        application.setApplicationStatus(newStatus);
        applicationRepository.save(application);

        saveApplicationStatusHistory(application, newStatus);

        CreateNotiRequest notificationRequest = CreateNotiRequest.builder()
                .applicationId(application.getApplicationId())
                .userId(application.getJobSeekerProfile().getUser().getUserId())
                .content(newStatus.toString())
                .build();

        NotificationDTO notification = notificationServiceImpl.createNoti(notificationRequest);
        messagingTemplate.convertAndSend(DefaultValue.WS_TOPIC_NOTIFICATION  + application.getJobSeekerProfile().getUser().getUserId(), notification);
    }

    // Lấy 5 apply gần nhất
    @Override
    public List<RecentApplicationDTO> getRecentApplications() {
        List<Application> applications = applicationRepository.findTop5ByOrderByAppliedAtDesc();
        List<RecentApplicationDTO> dtos = applications.stream().map(app -> {
            RecentApplicationDTO dto = RecentApplicationDTO.builder()
                    .id(app.getApplicationId())
                    .applicant(app.getJobSeekerProfile().getFirstName() + " " + app.getJobSeekerProfile().getLastName())
                    .jobTitle(app.getJob().getTitle())
                    .status(app.getApplicationStatus().toString())
                    .date(app.getAppliedAt().toString())
                    .build();
            return dto;
        }).collect(Collectors.toList());
        return dtos;
    }

    //Lấy các apply nổi bật
    @Override
    public ChartDataDTO getApplicationTrends(String type, Integer month) {
        ChartDataDTO dto = new ChartDataDTO();
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        if ("range".equals(type)) {
            List<String> labels = new ArrayList<>();
            List<Long> counts = new ArrayList<>();
            for (int m = 1; m <= now.getMonthValue(); m++) {
                LocalDateTime start = LocalDateTime.of(currentYear, m, 1, 0, 0);
                LocalDateTime end = start.plusMonths(1);
                long count = applicationRepository.countByAppliedAtBetween(start, end);
                labels.add(Month.of(m).name().substring(0, 3));
                counts.add(count);
            }
            dto.setLabels(labels);
            dto.setCounts(counts);

        } else if ("month".equals(type) && month != null) {
            YearMonth yearMonth = YearMonth.of(currentYear, month);
            List<String> labels = new ArrayList<>();
            List<Long> counts = new ArrayList<>();
            for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
                LocalDateTime start = LocalDateTime.of(currentYear, month, day, 0, 0);
                LocalDateTime end = start.plusDays(1);
                long count = applicationRepository.countByAppliedAtBetween(start, end);
                labels.add(String.format("%02d", day));
                counts.add(count);
            }
            dto.setLabels(labels);
            dto.setCounts(counts);

        } else {
            throw new BadRequestException("Invalid type or month");
        }

        return dto;
    }

    //Thống kê số lượng apply của từng khu vực
    @Override
    public RegionChartDataDTO getActiveRegions(String type, Integer month) {
        RegionChartDataDTO dto = new RegionChartDataDTO();
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        List<Object[]> results;
        if ("range".equals(type)) {
            LocalDateTime start = LocalDateTime.of(currentYear, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(currentYear, now.getMonthValue() + 1, 1, 0, 0);
            results = applicationRepository.countByLocationAndCreatedAtBetween(start, end);
        } else if ("month".equals(type) && month != null) {
            LocalDateTime start = LocalDateTime.of(currentYear, month, 1, 0, 0);
            LocalDateTime end = start.plusMonths(1);
            results = applicationRepository.countByLocationAndCreatedAtBetween(start, end);
        } else {
            throw new BadRequestException("Invalid type or month");
        }

        List<String> labels = results.stream().map(row -> (String) row[0]).collect(Collectors.toList());
        List<Long> counts = results.stream().map(row -> ((Number) row[1]).longValue()).collect(Collectors.toList());
        dto.setLabels(labels);
        dto.setCounts(counts);

        return dto;
    }

    //Thống kê số job của từng công ty
    @Override
    public CompanyJobStatsDTO getCompanyJobStats(String type, Integer month) {
        CompanyJobStatsDTO dto = new CompanyJobStatsDTO();
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        List<Object[]> results;

        if ("range".equals(type)) {
            LocalDateTime start = LocalDateTime.of(currentYear, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(currentYear, now.getMonthValue() + 1, 1, 0, 0);
            results = jobRepository.countJobsByCompanyAndCreatedAtBetween(start, end);
        } else if ("month".equals(type) && month != null) {
            LocalDateTime start = LocalDateTime.of(currentYear, month, 1, 0, 0);
            LocalDateTime end = start.plusMonths(1);
            results = jobRepository.countJobsByCompanyAndCreatedAtBetween(start, end);
        } else {
            throw new BadRequestException("Invalid type or month");
        }

        List<String> labels = results.stream().map(row -> (String) row[1]).collect(Collectors.toList());
        List<Long> counts = results.stream().map(row -> ((Number) row[2]).longValue()).collect(Collectors.toList());
        dto.setLabels(labels);
        dto.setCounts(counts);

        return dto;
    }

}
