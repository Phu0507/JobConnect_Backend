package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.response.IndustryReponse;

import java.util.List;

public interface ICompanyIndustryService {
    List<IndustryReponse> getAllCompanyIndustries();
    void addCompanyIndustry(String industryName);
    void deleteCompanyIndustry(Integer industryId);
    void updateCompanyIndustry(Integer industryId, String industryName);
}
