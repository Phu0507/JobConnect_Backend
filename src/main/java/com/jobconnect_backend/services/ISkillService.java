package com.jobconnect_backend.services;

import com.jobconnect_backend.entities.Skill;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ISkillService {
    List<Skill> getAllSkills();
}
