package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.request.AuthRequest;
import com.jobconnect_backend.dto.request.RegistrationRequest;
import com.jobconnect_backend.dto.request.VerifyOtpRequest;
import com.jobconnect_backend.dto.response.AuthResponse;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import java.io.IOException;

@Service
public interface IAuthService {
    void register(RegistrationRequest registrationRequest, BindingResult result) throws IOException;
    AuthResponse login(AuthRequest authRequest, BindingResult result);
    void resendOtp(String email);
    void verifyOtp(VerifyOtpRequest request);
}
