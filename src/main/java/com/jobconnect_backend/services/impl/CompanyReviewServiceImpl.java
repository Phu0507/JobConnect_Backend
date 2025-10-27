package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.dto.response.CompanyReviewResponse;
import com.jobconnect_backend.entities.CompanyReview;
import com.jobconnect_backend.repositories.CompanyRepository;
import com.jobconnect_backend.repositories.CompanyReviewRepository;
import com.jobconnect_backend.repositories.JobSeekerProfileRepository;
import com.jobconnect_backend.services.ICompanyReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyReviewServiceImpl implements ICompanyReviewService {
    private final CompanyReviewRepository companyReviewRepository;

    @Override
    public List<CompanyReviewResponse> getCompanyReviews(Integer companyId) {
        List<CompanyReview> reviews = companyReviewRepository.findByCompanyCompanyId(companyId);

        //only return approved reviews
        reviews = reviews.stream().filter(CompanyReview::getIsApproved).collect(Collectors.toList());

        return reviews.stream()
                .map(review -> CompanyReviewResponse.builder()
                        .reviewId(review.getReviewId())
                        .companyId(review.getCompany().getCompanyId())
                        .rating(review.getRating())
                        .reviewText(review.getReviewText())
                        .reviewDate(review.getReviewDate())
                        .jobSeekerProfileId(review.getJobSeekerProfile().getProfileId())
                        .jobSeekerProfileName(review.getJobSeekerProfile().getFirstName().concat(" ").concat(review.getJobSeekerProfile().getLastName()))
                        .build(
                        ))
                .collect(Collectors.toList());
    }

}
