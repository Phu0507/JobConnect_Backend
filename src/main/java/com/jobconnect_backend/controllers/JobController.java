package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.dto.JobDTO;
import com.jobconnect_backend.dto.request.CreateJobRequest;
import com.jobconnect_backend.dto.request.RejectJobRequest;
import com.jobconnect_backend.dto.request.UpdateJobRequest;
import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.services.IJobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    private IJobService jobServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        List<JobDTO> jobs = jobServiceImpl.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @PostMapping("/create")
    public ResponseEntity<SuccessResponse> createJob(@Valid @RequestBody CreateJobRequest request, BindingResult bindingResult) {
        jobServiceImpl.createJob(request, bindingResult);
        return ResponseEntity.ok(new SuccessResponse("Job created successfully"));
    }

    @PutMapping("/update")
    public ResponseEntity<SuccessResponse> updateJob(@Valid @RequestBody UpdateJobRequest request, BindingResult bindingResult) {
        jobServiceImpl.updateJob(request, bindingResult);
        return ResponseEntity.ok(new SuccessResponse("Job updated successfully"));
    }

    @DeleteMapping("/delete/{jobId}")
    public ResponseEntity<SuccessResponse> deleteJob(@PathVariable Integer jobId) {
        jobServiceImpl.deleteJob(jobId);
        return ResponseEntity.ok(new SuccessResponse("Job deleted successfully"));
    }

    @PutMapping("/approve/{jobId}")
    public ResponseEntity<SuccessResponse> approveJob(@PathVariable Integer jobId) {
        jobServiceImpl.approveJob(jobId);
        return ResponseEntity.ok(new SuccessResponse("Job approved successfully"));
    }

    @PutMapping("/reject")
    public ResponseEntity<SuccessResponse> rejectJob(@RequestBody RejectJobRequest request) {
        jobServiceImpl.rejectJob(request);
        return ResponseEntity.ok(new SuccessResponse("Job rejected successfully"));
    }
}
