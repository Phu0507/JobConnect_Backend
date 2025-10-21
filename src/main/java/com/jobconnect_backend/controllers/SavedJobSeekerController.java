package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.services.ISavedJobSeekerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
