package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.request.UpdatePersonalInfoRequest;
import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.services.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userServiceImpl;

    @PostMapping("/update-profile")
    public ResponseEntity<SuccessResponse> updateProfile(@Valid @ModelAttribute UpdatePersonalInfoRequest updatePersonalInfoRequest, BindingResult bindingResult) {
        userServiceImpl.updateProfileInfo(updatePersonalInfoRequest, bindingResult);
        return ResponseEntity.ok(new SuccessResponse("User profile updated successfully"));
    }
}
