package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.dto.JobSeekerProfileDTO;
import com.jobconnect_backend.dto.request.CreateWorkExperienceRequest;
import com.jobconnect_backend.dto.request.SkillRequest;
import com.jobconnect_backend.dto.request.UpdateWorkExperienceRequest;
import com.jobconnect_backend.dto.response.JobSeekerProfileResponse;
import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.services.IJobSeekerProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobseeker")
public class JobSeekerProfileController {
    @Autowired
    private IJobSeekerProfileService jobSeekerProfileServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<List<JobSeekerProfileDTO>> getAllJobSeekerProfiles() {
        return ResponseEntity.ok(jobSeekerProfileServiceImpl.getAllJobSeekerProfiles());
    }

    @GetMapping("/getProfileByUserId")
    public ResponseEntity<JobSeekerProfileResponse> getJobSeekerProfile(@RequestParam Integer userId) {
        return ResponseEntity.ok(jobSeekerProfileServiceImpl.getProfileByUserId(userId));
    }

    @GetMapping("/getProfileById")
    public ResponseEntity<JobSeekerProfileDTO> getJobSeekerProfileById(@RequestParam Integer jobSeekerId) {
        return ResponseEntity.ok(jobSeekerProfileServiceImpl.getProfileById(jobSeekerId));
    }

    @PostMapping("/addWorkExperience")
    public ResponseEntity<SuccessResponse> createWorkExperience(@RequestParam Integer jobSeekerId, @Valid @RequestBody CreateWorkExperienceRequest createWorkExperienceRequest, BindingResult result) {
        jobSeekerProfileServiceImpl.addWorkExperience(jobSeekerId, createWorkExperienceRequest, result);
        return ResponseEntity.ok(new SuccessResponse("Work Experience created successfully"));
    }

    @PostMapping("/updateWorkExperience")
    public ResponseEntity<SuccessResponse> updateWorkExperience(@RequestParam Integer jobSeekerId, @Valid @RequestBody UpdateWorkExperienceRequest updateWorkExperienceRequest, BindingResult result) {
        jobSeekerProfileServiceImpl.updateWorkExperience(jobSeekerId, updateWorkExperienceRequest, result);
        return ResponseEntity.ok(new SuccessResponse("Work Experience updated successfully"));
    }

    @DeleteMapping("/deleteWorkExperience")
    public ResponseEntity<SuccessResponse> deleteWorkExperience(@RequestParam Integer jobSeekerId, @RequestParam Integer workExperienceId) {
        jobSeekerProfileServiceImpl.deleteWorkExperience(jobSeekerId, workExperienceId);
        return ResponseEntity.ok(new SuccessResponse("Work Experience deleted successfully"));
    }

    // truyen vao id user
    @PostMapping("/addSkill")
    public ResponseEntity<SuccessResponse> createSkills(@Valid @RequestBody SkillRequest createSkillsRequest, BindingResult result) {
        jobSeekerProfileServiceImpl.addSkills(createSkillsRequest, result);
        return ResponseEntity.ok(new SuccessResponse("Skill created successfully"));
    }

    @PostMapping("/updateSkill")
    public ResponseEntity<SuccessResponse> updateSkills(@Valid @RequestBody SkillRequest skillRequest, BindingResult bindingResult) {
        jobSeekerProfileServiceImpl.updateSkills(skillRequest, bindingResult);
        return ResponseEntity.ok(new SuccessResponse("Skill updated successfully"));
    }
}
