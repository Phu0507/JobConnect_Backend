package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.ChartDataDTO;
import com.jobconnect_backend.dto.dto.CompanyJobStatsDTO;
import com.jobconnect_backend.dto.dto.RecentApplicationDTO;
import com.jobconnect_backend.dto.dto.RegionChartDataDTO;
import com.jobconnect_backend.dto.request.ApplicationRequest;
import com.jobconnect_backend.dto.response.ApplicationOfJobResponse;
import com.jobconnect_backend.dto.response.ApplicationStatusResponse;

import java.util.List;

public interface IApplicationService {
    List<ApplicationStatusResponse> getAllApplications();
    void applyForJob(ApplicationRequest request);
    List<ApplicationOfJobResponse> getApplicationOfJob(Integer jobId);
    List<ApplicationStatusResponse> getApplicationOfJobByJobSeeker(Integer jobSeekerId);
    ApplicationStatusResponse getApplicationStatusHistory(Integer applicationId);
    void updateApplicationStatus(Integer applicationId, String status);
    List<RecentApplicationDTO> getRecentApplications();
    ChartDataDTO getApplicationTrends(String type, Integer month);
    
    CompanyJobStatsDTO getCompanyJobStats(String type, Integer month);
}
