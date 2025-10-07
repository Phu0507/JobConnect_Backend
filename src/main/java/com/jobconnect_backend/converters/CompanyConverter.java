package com.jobconnect_backend.converters;

import com.jobconnect_backend.dto.dto.CompanyDTO;
import com.jobconnect_backend.entities.Company;
import com.jobconnect_backend.populators.CompanyPopulator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CompanyConverter {
    private final CompanyPopulator companyPopulator;

    public CompanyDTO convertToCompanyDTO(Company company){
        CompanyDTO dto = new CompanyDTO();
        companyPopulator.populate(company, dto);
        return dto;
    }
}
