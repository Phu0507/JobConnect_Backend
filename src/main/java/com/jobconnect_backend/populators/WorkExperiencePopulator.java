package com.jobconnect_backend.populators;

import com.jobconnect_backend.converters.SkillConverter;
import com.jobconnect_backend.dto.dto.WorkExperienceDTO;
import com.jobconnect_backend.entities.JobCategory;
import com.jobconnect_backend.entities.WorkExperience;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class WorkExperiencePopulator {
    private final SkillConverter skillConverter;
    public void populate(WorkExperience source, WorkExperienceDTO target) {
        target.setId(source.getWorkExperienceId());
        target.setJobPositionId(source.getJobPosition().getJobPositionId());
        target.setCompanyId(source.getCompany().getCompanyId());
        target.setCompanyName(source.getCompany().getCompanyName());
        target.setStartDate(source.getStartDate().toString());
        target.setEndDate(source.getEndDate().toString());
        target.setJobTitle(source.getJobPosition().getName());
        target.setLogo(source.getCompany().getLogoPath());
        target.setJobType(source.getJobType());
        target.setDescription(source.getDescription());
        target.setSkills(source.getSkills().stream()
                .map(skillConverter::convertToSkillDTO)
                .collect(Collectors.toList()));
        List<JobCategory> categoryDTOList = source.getCategories().stream()
                .map(category -> JobCategory.builder()
                        .jobCategoryId(category.getJobCategoryId())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());
        target.setCategories(categoryDTOList);
    }
}
