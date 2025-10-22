package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.CompanyDTO;

import java.util.List;

public interface ICompanyService {
    List<CompanyDTO> getAllCompanies();
    CompanyDTO getCompanyById(Integer companyId);
    List<CompanyDTO> findCompanyByIndustryAndCompanyName(Integer industryId, String companyName);
}
