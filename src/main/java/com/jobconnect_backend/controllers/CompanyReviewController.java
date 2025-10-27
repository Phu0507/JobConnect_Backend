package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.response.CompanyReviewResponse;
import com.jobconnect_backend.services.ICompanyReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/company/review")
@RequiredArgsConstructor
public class CompanyReviewController {
    private final ICompanyReviewService companyReviewServiceImpl;

    @GetMapping("/getListReviews/{companyId}")
    public ResponseEntity<List<CompanyReviewResponse>> getCompanyReviews(@PathVariable Integer companyId) {
        List<CompanyReviewResponse> companyReviewResponse = companyReviewServiceImpl.getCompanyReviews(companyId);
        return ResponseEntity.ok(companyReviewResponse);
    }
}
