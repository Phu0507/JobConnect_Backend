package com.jobconnect_backend.populators;

import com.jobconnect_backend.dto.dto.CompanyDTO;
import com.jobconnect_backend.dto.response.IndustryReponse;
import com.jobconnect_backend.entities.Company;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompanyPopulator {
    public void populate(Company source, CompanyDTO target) {
        target.setCompanyId(source.getCompanyId());
        target.setCompanyName(source.getCompanyName());
        target.setDescription(source.getDescription());
        target.setLogoPath(source.getLogoPath());
        List<IndustryReponse> industryReponse = source.getIndustry().stream()
                .map(industry -> IndustryReponse.builder()
                        .industryId(industry.getIndustryId())
                        .name(industry.getName())
                        .build())
                .toList();
        target.setIndustry(industryReponse);
        target.setWebsite(source.getWebsite());
        target.setEmail(source.getUser().getEmail());
        target.setPhoneNumber(source.getUser().getPhone());
        target.setVip(source.getUser().isVip());
        target.setVipLevel(source.getUser().getVipLevel());
        target.setVipExpiryDate(source.getUser().getVipExpiryDate());
        target.setCreateJobCount(source.getCreateJobCount());
    }
}

