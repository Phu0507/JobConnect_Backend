package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.request.AuthRequest;
import com.jobconnect_backend.dto.request.RegistrationRequest;
import com.jobconnect_backend.dto.request.VerifyOtpRequest;
import com.jobconnect_backend.dto.response.AuthResponse;
import com.jobconnect_backend.dto.response.SuccessResponse;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.services.IAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

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

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest, BindingResult result) {
        AuthResponse authResponse = authServiceImpl.login(authRequest, result);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<SuccessResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        authServiceImpl.verifyOtp(request);
        return ResponseEntity.ok(new SuccessResponse("OTP verified successfully"));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<SuccessResponse> resendOtp(
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        authServiceImpl.resendOtp(email);
        return ResponseEntity.ok(new SuccessResponse("OTP resent successfully"));
    }
}
