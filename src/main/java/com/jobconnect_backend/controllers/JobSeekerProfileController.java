package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.services.IJobSeekerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jobseeker")
public class JobSeekerProfileController {
    @Autowired
    private IJobSeekerProfileService jobSeekerProfileServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<List<JobSeekerProfileDTO>> getAllJobSeekerProfiles() {
        return ResponseEntity.ok(jobSeekerProfileServiceImpl.getAllJobSeekerProfiles());
    }
}
