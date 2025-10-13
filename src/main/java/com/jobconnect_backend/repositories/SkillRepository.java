package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
    boolean existsByName(String name);
}
