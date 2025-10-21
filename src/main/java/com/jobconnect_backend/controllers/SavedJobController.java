package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.response.SavedJobResponse;
import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.services.ISavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/savedJob")
@RequiredArgsConstructor
public class SavedJobController {
    private final ISavedJobService savedJobServiceImpl;

    @GetMapping("/listSavedJobs")
    public ResponseEntity<List<SavedJobResponse>> getListSavedJobs(@RequestParam Integer jobSeekerProfileId) {
        return ResponseEntity.ok(savedJobServiceImpl.getListSavedJobs(jobSeekerProfileId));
    }

    @PostMapping("/save")
    public ResponseEntity<SuccessResponse> saveJob(@RequestParam Integer jobId, @RequestParam Integer jobSeekerProfileId) {
        savedJobServiceImpl.saveJob(jobId, jobSeekerProfileId);
        return ResponseEntity.ok(new SuccessResponse("Job saved successfully"));
    }

    @PostMapping("/unsave")
    public ResponseEntity<SuccessResponse> unsaveJob(@RequestParam Integer jobId, @RequestParam Integer jobSeekerProfileId) {
        savedJobServiceImpl.unsaveJob(jobId, jobSeekerProfileId);
        return ResponseEntity.ok(new SuccessResponse("Job unsaved successfully"));
    }
}
