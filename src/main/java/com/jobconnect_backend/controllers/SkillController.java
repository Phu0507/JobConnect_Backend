package com.jobconnect_backend.controllers;

import com.jobconnect_backend.entities.Skill;
import com.jobconnect_backend.services.ISkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
public class SkillController {
    private final ISkillService skillServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillServiceImpl.getAllSkills());
    }
}
