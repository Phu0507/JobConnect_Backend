package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.response.IndustryReponse;
import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.services.ICompanyIndustryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/delete/{companyIndustryId}")
    public ResponseEntity<SuccessResponse> deleteCompanyIndustry(@PathVariable Integer companyIndustryId) {
        companyIndustryServiceImpl.deleteCompanyIndustry(companyIndustryId);
        return ResponseEntity.ok(new SuccessResponse("Company industry deleted successfully"));
    }

    @PutMapping("/update")
    public ResponseEntity<SuccessResponse> updateCompanyIndustry(
            @RequestParam Integer companyIndustryId,
            @RequestParam String companyIndustryName
    ) {
        companyIndustryServiceImpl.updateCompanyIndustry(companyIndustryId, companyIndustryName);
        return ResponseEntity.ok(new SuccessResponse("Company industry updated successfully"));
    }
}
