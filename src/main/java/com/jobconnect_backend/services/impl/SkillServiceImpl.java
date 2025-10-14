package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.entities.Skill;
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
}
