package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.CompanyConverter;
import com.jobconnect_backend.dto.dto.CompanyDTO;
import com.jobconnect_backend.entities.Company;
import com.jobconnect_backend.repositories.CompanyRepository;
import com.jobconnect_backend.repositories.UserRepository;
import com.jobconnect_backend.services.ICompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements ICompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyConverter companyConverter;

    @Override
    public List<CompanyDTO> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream().map(companyConverter::convertToCompanyDTO).toList();
    }
}
