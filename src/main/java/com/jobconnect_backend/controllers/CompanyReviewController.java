package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.request.AddCompanyReviewRequest;
import com.jobconnect_backend.dto.request.UpdateCompanyReviewRequest;
import com.jobconnect_backend.dto.response.CompanyReviewResponse;
import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.services.ICompanyReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public ResponseEntity<SuccessResponse> addCompanyReview(@RequestBody AddCompanyReviewRequest addCompanyReviewRequest) {
        companyReviewServiceImpl.addCompanyReview(addCompanyReviewRequest);
        return ResponseEntity.ok(new SuccessResponse("Company review added successfully"));
    }

    @PutMapping("/update")
    public ResponseEntity<SuccessResponse> updateCompanyReview(@RequestBody UpdateCompanyReviewRequest updateCompanyReviewRequest) {
        companyReviewServiceImpl.updateCompanyReview(updateCompanyReviewRequest);
        return ResponseEntity.ok(new SuccessResponse("Company review updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<SuccessResponse> deleteCompanyReview(@RequestParam Integer reviewId) {
        companyReviewServiceImpl.deleteCompanyReview(reviewId);
        return ResponseEntity.ok(new SuccessResponse("Company review deleted successfully"));
    }
}
