package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.services.ISavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/savedJob")
@RequiredArgsConstructor
public class SavedJobController {
    private final ISavedJobService savedJobServiceImpl;

    @PostMapping("/save")
    public ResponseEntity<SuccessResponse> saveJob(@RequestParam Integer jobId, @RequestParam Integer jobSeekerProfileId) {
        savedJobServiceImpl.saveJob(jobId, jobSeekerProfileId);
        return ResponseEntity.ok(new SuccessResponse("Job saved successfully"));
    }
}
