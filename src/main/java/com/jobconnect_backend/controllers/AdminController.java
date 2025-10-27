package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.dto.*;
import com.jobconnect_backend.repositories.ApplicationRepository;
import com.jobconnect_backend.repositories.JobRepository;
import com.jobconnect_backend.repositories.UserRepository;
import com.jobconnect_backend.services.IApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final IApplicationService applicationServiceImpl;

    @GetMapping("/applications/recent")
    public ResponseEntity<List<RecentApplicationDTO>> getRecentApplications() {
        List<RecentApplicationDTO> dtos = applicationServiceImpl.getRecentApplications();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/applications/trends")
    public ResponseEntity<ChartDataDTO> getApplicationTrends(
            @RequestParam String type,
            @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(applicationServiceImpl.getApplicationTrends(type, month));
    }

    @GetMapping("/regions/active")
    public ResponseEntity<RegionChartDataDTO> getActiveRegions(
            @RequestParam String type,
            @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(applicationServiceImpl.getActiveRegions(type, month));
    }

    @GetMapping("/companies/job-stats")
    public ResponseEntity<CompanyJobStatsDTO> getCompanyJobStats(
            @RequestParam String type,
            @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(applicationServiceImpl.getCompanyJobStats(type, month));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .totalApplications(applicationRepository.count())
                .openJobs(
                        jobRepository.findAll().stream()
                                .filter(job -> job.getIsActive() && !job.getIsDeleted() && job.getIsApproved())
                                .count()
                )
                .activeUsers(userRepository.countByIsVerifiedIsTrue())
                .pendingApprovals(jobRepository.countByIsApprovedIsFalse())
                .build();
        return ResponseEntity.ok(stats);
    }
}
