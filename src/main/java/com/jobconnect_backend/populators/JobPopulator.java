package com.jobconnect_backend.populators;

import com.jobconnect_backend.converters.CompanyConverter;
import com.jobconnect_backend.converters.SkillConverter;
import com.jobconnect_backend.dto.dto.JobDTO;
import com.jobconnect_backend.dto.dto.SkillDTO;
import com.jobconnect_backend.entities.Job;
import com.jobconnect_backend.entities.JobCategory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@AllArgsConstructor
@Component
public class JobPopulator {
    private final SkillConverter skillConverter;
    private final CompanyConverter companyConverter;
    public void populate(Job source, JobDTO target) {
        target.setJobId(source.getJobId());
        target.setCompany(companyConverter.convertToCompanyDTO(source.getCompany()));
        target.setTitle(source.getTitle());
        target.setDescription(source.getDescription());
        target.setRequirements(source.getRequirements());
        target.setBenefits(source.getBenefits());
        target.setSalaryMin(source.getSalaryMin());
        target.setSalaryMax(source.getSalaryMax());
        target.setJobType(source.getJobType().name());
        target.setLocation(source.getLocation());
        target.setEducationLevel(source.getEducationLevel());
        target.setYearsOfExperience(source.getYearsOfExperience());
        target.setPostedAt(source.getPostedAt());
        target.setDeadline(source.getDeadline());
        target.setIsActive(source.getIsActive());
        target.setIsApproved(source.getIsApproved());
        target.setNote(source.getNote());
        target.setIsPending(source.getIsPending());
        target.setIsDeleted(source.getIsDeleted());
        target.setIsPriority(source.getIsPriority());
        target.setPriorityLevel(source.getPriorityLevel());
        target.setExpired(source.getIsExpired());

        List<SkillDTO> skillDTOList = source.getSkills().stream()
                .map(skillConverter::convertToSkillDTO)
                .collect(Collectors.toList());
        target.setSkills(skillDTOList);

        List<JobCategory> categoryDTOList = source.getCategories().stream()
                .map(category -> JobCategory.builder()
                        .jobCategoryId(category.getJobCategoryId())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());
        target.setCategories(categoryDTOList);
    }
}

