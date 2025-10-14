package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.entities.Skill;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.SkillRepository;
import com.jobconnect_backend.services.ISkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements ISkillService {
    private final SkillRepository skillRepository;

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Override
    public void addSkill(String skillName) {
        if(skillRepository.existsByName(skillName)){
            throw new BadRequestException("Skill already exists");
        }
        Skill skill = Skill.builder()
                .name(skillName)
                .build();
        skillRepository.save(skill);
    }

    @Override
    public void deleteSkill(Integer skillId) {
        skillRepository.deleteById(skillId);
    }

    @Override
    public void updateSkill(Integer skillId, String skillName) {
        Skill skill = skillRepository.findById(skillId).orElseThrow(
                () -> new BadRequestException("Skill not found"));
        skill.setName(skillName);
        skillRepository.save(skill);
    }
}
