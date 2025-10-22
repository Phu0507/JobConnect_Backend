package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.dto.CompanyDTO;
import com.jobconnect_backend.services.ICompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {
    private final ICompanyService companyServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyServiceImpl.getAllCompanies());
    }
}
