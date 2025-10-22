package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.CompanyDTO;
import com.jobconnect_backend.dto.request.CardRequest;
import com.jobconnect_backend.dto.response.CardInfoResponse;

import java.util.List;

public interface ICompanyService {
    List<CompanyDTO> getAllCompanies();
    CompanyDTO getCompanyById(Integer companyId);
    List<CompanyDTO> findCompanyByIndustryAndCompanyName(Integer industryId, String companyName);
    void createCardPaymentForCompany(CardRequest cardRequest);
    CardInfoResponse getCardInfoByCompanyId(Integer userId);
}
