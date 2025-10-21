package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.services.ISavedJobSeekerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/savedJobSeeker")
@RequiredArgsConstructor
public class SavedJobSeekerController {
    private final ISavedJobSeekerService savedJobSeekerServiceImpl;

    @GetMapping("/listSavedJobSeekers")
    public ResponseEntity<List<JobSeekerProfileDTO>> getListSavedJobSeekers(Integer companyId) {
        List<JobSeekerProfileDTO> list = savedJobSeekerServiceImpl.getListSavedJobSeekers(companyId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/save")
    public ResponseEntity<SuccessResponse> saveJobSeeker(Integer jobSeekerProfileId, Integer companyId) {
        savedJobSeekerServiceImpl.saveJobSeeker(jobSeekerProfileId, companyId);
        return ResponseEntity.ok(new SuccessResponse("Job Seeker saved successfully"));
    }

    @PostMapping("/unsave")
    public ResponseEntity<SuccessResponse> unsaveJobSeeker(Integer jobSeekerProfileId, Integer companyId) {
        savedJobSeekerServiceImpl.unsaveJobSeeker(jobSeekerProfileId, companyId);
        return ResponseEntity.ok(new SuccessResponse("Job Seeker unsaved successfully"));
    }
}
