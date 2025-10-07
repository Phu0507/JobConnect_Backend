package com.jobconnect_backend.converters;

import com.jobconnect_backend.dto.dto.SkillDTO;
import com.jobconnect_backend.entities.Skill;
import com.jobconnect_backend.populators.SkillPopulator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class SkillConverter {
    private final SkillPopulator skillPopulator;

    public SkillDTO convertToSkillDTO(Skill skill) {
        SkillDTO dto = new SkillDTO();
        skillPopulator.populate(skill, dto);
        return dto;
    }
}