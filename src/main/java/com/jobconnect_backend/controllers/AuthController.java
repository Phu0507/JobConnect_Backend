package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.request.RegistrationRequest;
import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.services.IAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private IAuthService authServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(@Valid @ModelAttribute RegistrationRequest registrationRequest, BindingResult result) throws IOException {
        authServiceImpl.register(registrationRequest, result);
        return ResponseEntity.ok(new SuccessResponse("User registered successfully"));
    }
}
