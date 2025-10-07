package com.jobconnect_backend.populators;

import com.jobconnect_backend.dto.dto.SkillDTO;
import com.jobconnect_backend.entities.Skill;
import org.springframework.stereotype.Component;

@Component
public class SkillPopulator {
    public void populate(Skill source, SkillDTO target) {
        target.setSkillId(source.getSkillId());
        target.setName(source.getName());
    }
}
