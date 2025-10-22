package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.response.IndustryReponse;
import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.services.ICompanyIndustryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/companyIndustry")
@RequiredArgsConstructor
public class CompanyIndustryController {
    private final ICompanyIndustryService companyIndustryServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<List<IndustryReponse>> getAllCompanyIndustries() {
        return ResponseEntity.ok(companyIndustryServiceImpl.getAllCompanyIndustries());
    }

    @PostMapping("/add")
    public ResponseEntity<SuccessResponse> addCompanyIndustry(String companyIndustryName) {
        companyIndustryServiceImpl.addCompanyIndustry(companyIndustryName);
        return ResponseEntity.ok(new SuccessResponse("Company industry added successfully"));
    }
}
