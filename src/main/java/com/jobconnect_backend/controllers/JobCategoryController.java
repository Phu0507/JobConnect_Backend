package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.response.JobCategoryResponse;
import com.jobconnect_backend.services.IJobCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jobCategory")
@RequiredArgsConstructor
public class JobCategoryController {
    private final IJobCategoryService jobCategoryServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<List<JobCategoryResponse>> getAllJobCategories() {
        return ResponseEntity.ok(jobCategoryServiceImpl.getAllJobCategories());
    }
}
