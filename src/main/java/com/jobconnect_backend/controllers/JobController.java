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
import java.util.Map;

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

    @GetMapping("/searchJobs")
    public ResponseEntity<List<JobDTO>> searchJobs(@RequestParam(required = false) String keyword, @RequestParam(required = false) List<String> location, @RequestParam(required = false) List<Integer> jobCategoryId) {
        List<JobDTO> jobs = jobServiceImpl.searchJobs(keyword, location, jobCategoryId);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping ("/getJobById/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Integer id) {
        JobDTO job = jobServiceImpl.getJobByID(id);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<JobDTO>> getJobsByCompanyId(@PathVariable Integer companyId,
                                                           @RequestParam Integer id) {
        return ResponseEntity.ok(jobServiceImpl.getJobsByCompanyId(companyId, id));
    }

    @GetMapping("/{jobSeekerId}/skills-and-categories")
    public ResponseEntity<Map<String, List<Integer>>> getSkillsAndCategories(@PathVariable Integer jobSeekerId) {
        Map<String, List<Integer>> skillsAndCategories = jobServiceImpl.getSkillsAndCategories(jobSeekerId);
        return ResponseEntity.ok(skillsAndCategories);
    }

}
