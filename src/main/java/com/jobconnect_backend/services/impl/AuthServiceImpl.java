package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.config.AwsS3Service;
import com.jobconnect_backend.config.EmailConfig;
import com.jobconnect_backend.defaults.DefaultValue;
import com.jobconnect_backend.dto.request.AuthRequest;
import com.jobconnect_backend.dto.request.RegistrationRequest;
import com.jobconnect_backend.dto.request.VerifyOtpRequest;
import com.jobconnect_backend.dto.response.AuthResponse;
import com.jobconnect_backend.entities.JobSeekerProfile;
import com.jobconnect_backend.entities.User;
import com.jobconnect_backend.entities.enums.Role;
import com.jobconnect_backend.repositories.JobSeekerProfileRepository;
import com.jobconnect_backend.repositories.UserRepository;
import com.jobconnect_backend.services.IAuthService;
import com.jobconnect_backend.utils.ValidateField;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidateField validateField;
    private final AwsS3Service awsS3Service;
    private final EmailConfig emailConfig;
    @Override
    public void register(RegistrationRequest registrationRequest, BindingResult result) throws IOException {
        Map<String, String> errors = validateField.getErrors(result);
        if (registrationRequest.getRole() == Role.JOBSEEKER) {
            validateField.getJobSeekerFieldErrors(errors, registrationRequest.getFirstName(), registrationRequest.getLastName());
        } else if (registrationRequest.getRole() == Role.COMPANY) {
            validateField.getCompanyFieldErrors(errors, registrationRequest.getCompanyName());
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Please complete all required fields to proceed.", errors);
        }

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        if (registrationRequest.getRole() == Role.JOBSEEKER) {
            User user = User.builder()
                    .email(registrationRequest.getEmail())
                    .passwordHash(passwordEncoder.encode(registrationRequest.getPassword()))
                    .phone(registrationRequest.getPhone())
                    .role(registrationRequest.getRole())
                    .createdAt(LocalDateTime.now())
                    .isVerified(true)
                    .build();

            userRepository.save(user);

            jobSeekerProfileRepository.save(JobSeekerProfile.builder()
                    .firstName(registrationRequest.getFirstName())
                    .lastName(registrationRequest.getLastName())
                    .avatar(DefaultValue.AVATAR_URL_DEFAULT)
                    .user(user)
                    .build());
        } else if (registrationRequest.getRole() == Role.COMPANY) {
            String otp = String.format("%06d", new Random().nextInt(999999));
            LocalDateTime otpExpiry = LocalDateTime.now().plusMinutes(10);
            String logoPath = DefaultValue.AVATAR_URL_DEFAULT;
            if (registrationRequest.getLogoPath() != null && !registrationRequest.getLogoPath().isEmpty()) {
                try {
                    String originalFileName = registrationRequest.getLogoPath().getOriginalFilename();
                    String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                    String baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
                    String s3Key = baseName + "_" + System.currentTimeMillis() + extension;
                    logoPath = awsS3Service.uploadFileToS3(
                            registrationRequest.getLogoPath().getInputStream(),
                            s3Key,
                            registrationRequest.getLogoPath().getContentType()
                    );
                    System.out.println("S3 upload in register, logoPath: " + logoPath);
                } catch (IOException e) {
                    System.err.println("S3 upload failed in register: " + e.getMessage());
                    throw new BadRequestException("Failed to upload logo: " + e.getMessage());
                }
            } else {
                System.out.println("No logo file provided in register, using default: " + logoPath);
            }
            pendingRegistrations.put(registrationRequest.getEmail(), registrationRequest);
            pendingLogoPaths.put(registrationRequest.getEmail(), logoPath);
            pendingOtps.put(registrationRequest.getEmail(), otp);
            pendingOtpExpiries.put(registrationRequest.getEmail(), otpExpiry);

            try {
                emailService.sendOtpEmail(registrationRequest.getEmail(), otp);
            } catch (Exception e) {
                pendingRegistrations.remove(registrationRequest.getEmail());
                pendingOtps.remove(registrationRequest.getEmail());
                pendingOtpExpiries.remove(registrationRequest.getEmail());
                throw new BadRequestException("Failed to send OTP email: " + e.getMessage());
            }
        }
    }

    @Override
    public AuthResponse login(AuthRequest authRequest, BindingResult result) {
        return null;
    }

    @Override
    public void resendOtp(String email) {

    }

    @Override
    public void verifyOtp(VerifyOtpRequest request) {

    }
}
