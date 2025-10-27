package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.request.AddCompanyReviewRequest;
import com.jobconnect_backend.dto.request.UpdateCompanyReviewRequest;
import com.jobconnect_backend.dto.response.CompanyReviewResponse;

import java.util.List;

public interface ICompanyReviewService {
    List<CompanyReviewResponse> getCompanyReviews(Integer companyId);
    void addCompanyReview(AddCompanyReviewRequest addCompanyReviewRequest);
    void updateCompanyReview(UpdateCompanyReviewRequest updateCompanyReviewRequest);
    void deleteCompanyReview(Integer reviewId);
}
