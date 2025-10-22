package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.dto.response.IndustryReponse;
import com.jobconnect_backend.entities.Industry;
import com.jobconnect_backend.exception.BadRequestException;
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

    @Override
    public void addCompanyIndustry(String industryName) {
        if(industryRepository.existsByName(industryName)){
            throw new BadRequestException("Industry already exists");
        }

        Industry industry = Industry.builder()
                .name(industryName)
                .build();
        industryRepository.save(industry);
    }

    @Override
    public void deleteCompanyIndustry(Integer industryId) {

        industryRepository.deleteById(industryId);
    }

    @Override
    public void updateCompanyIndustry(Integer industryId, String industryName) {
        Industry industry = industryRepository.findById(industryId).orElseThrow(
                () -> new BadRequestException("Industry not found"));
        industry.setName(industryName);
        industryRepository.save(industry);
    }
}
