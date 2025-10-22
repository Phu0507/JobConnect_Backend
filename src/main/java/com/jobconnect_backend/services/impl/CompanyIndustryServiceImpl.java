package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.dto.response.IndustryReponse;
import com.jobconnect_backend.entities.Industry;
import com.jobconnect_backend.repositories.IndustryRepository;
import com.jobconnect_backend.services.ICompanyIndustryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyIndustryServiceImpl implements ICompanyIndustryService {
    private final IndustryRepository industryRepository;

    @Override
    public List<IndustryReponse> getAllCompanyIndustries() {
        List<Industry> industries = industryRepository.findAll();

        return industries.stream()
                .map(industry -> IndustryReponse.builder()
                        .industryId(industry.getIndustryId())
                        .name(industry.getName())
                        .build())
                .toList();
    }
}
