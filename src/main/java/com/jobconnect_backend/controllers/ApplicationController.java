package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.request.ApplicationRequest;
import com.jobconnect_backend.dto.response.ApplicationStatusResponse;
import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.services.impl.ApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
public class ApplicationController {
    @Autowired
    private ApplicationServiceImpl applicationServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<List<ApplicationStatusResponse>> getAllApplications() {
        List<ApplicationStatusResponse> response = applicationServiceImpl.getAllApplications();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply")
    public ResponseEntity<SuccessResponse> applyForJob(@RequestBody ApplicationRequest request) {
        applicationServiceImpl.applyForJob(request);
        return ResponseEntity.ok(new SuccessResponse("Application created successfully"));
    }
}
