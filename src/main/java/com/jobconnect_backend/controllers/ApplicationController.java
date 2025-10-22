package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.response.ApplicationStatusResponse;
import com.jobconnect_backend.services.impl.ApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
