package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.config.AwsS3Service;
import com.jobconnect_backend.config.EmailConfig;
import com.jobconnect_backend.config.JwtService;
import com.jobconnect_backend.defaults.DefaultValue;
import com.jobconnect_backend.dto.request.AuthRequest;
import com.jobconnect_backend.dto.request.RegistrationRequest;
import com.jobconnect_backend.dto.request.VerifyOtpRequest;
import com.jobconnect_backend.dto.response.AuthResponse;
import com.jobconnect_backend.entities.Company;
import com.jobconnect_backend.entities.Industry;
import com.jobconnect_backend.entities.JobSeekerProfile;
import com.jobconnect_backend.entities.User;
import com.jobconnect_backend.entities.enums.Role;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.CompanyRepository;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidateField validateField;
    private final AwsS3Service awsS3Service;
    private final EmailConfig emailConfig;
    private final JwtService jwtService;
    private final CompanyRepository companyRepository;

    private final Map<String, RegistrationRequest> pendingRegistrations = new HashMap<>();
    private final Map<String, String> pendingLogoPaths = new HashMap<>();
    private final Map<String, String> pendingOtps = new HashMap<>();
    private final Map<String, LocalDateTime> pendingOtpExpiries = new HashMap<>();

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

        if (registrationRequest.getRole() == Role.COMPANY) {
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
                emailConfig.sendOtpEmail(registrationRequest.getEmail(), otp);
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
        validateField.getErrors(result);

        if (!userRepository.existsByEmail(authRequest.getEmail())) {
            throw new BadRequestException("Email not exists");
        }

        User user = userRepository.findByEmail(authRequest.getEmail()).get();

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Password is incorrect");
        }

        String token = jwtService.generateToken(user.getEmail());

        Integer returnId = null;
        String avatar = null;
        Boolean isVip = false;

        switch (user.getRole()) {
            case JOBSEEKER:
                JobSeekerProfile profile = jobSeekerProfileRepository.findByUser_UserId(user.getUserId())
                        .orElseThrow(() -> new BadRequestException("JobSeekerProfile not found"));
                returnId = profile.getProfileId();
                avatar = profile.getAvatar();
                break;
            case COMPANY:
                Company company = companyRepository.findByUser_UserId(user.getUserId())
                        .orElseThrow(() -> new BadRequestException("Company not found"));
                returnId = company.getCompanyId();
                avatar = company.getLogoPath();
                isVip = user.isVip();
                break;
        }

        return AuthResponse.builder()
                .token(token)
                .id(user.getUserId())
                .userId(returnId)
                .email(user.getEmail())
                .avatar(avatar)
                .phone(user.getPhone())
                .role(user.getRole())
                .isVip(isVip)
                .build();
    }

    @Override
    public void verifyOtp(VerifyOtpRequest request) {
        if (!pendingRegistrations.containsKey(request.getEmail())) {
            throw new BadRequestException("No registration found for this email");
        }

        String storedOtp = pendingOtps.get(request.getEmail());
        LocalDateTime otpExpiry = pendingOtpExpiries.get(request.getEmail());

        if (storedOtp == null || otpExpiry == null) {
            throw new BadRequestException("No OTP found for this email");
        }

        if (LocalDateTime.now().isAfter(otpExpiry)) {
            pendingRegistrations.remove(request.getEmail());
            pendingLogoPaths.remove(request.getEmail());
            pendingOtps.remove(request.getEmail());
            pendingOtpExpiries.remove(request.getEmail());
            throw new BadRequestException("OTP has expired");
        }

        if (!storedOtp.equals(request.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        RegistrationRequest registrationRequest = pendingRegistrations.get(request.getEmail());
        String logoPath = pendingLogoPaths.getOrDefault(request.getEmail(), DefaultValue.AVATAR_URL_DEFAULT);

        User user = User.builder()
                .email(registrationRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registrationRequest.getPassword()))
                .phone(registrationRequest.getPhone())
                .role(registrationRequest.getRole())
                .createdAt(LocalDateTime.now())
                .isVerified(true)
                .isVip(false)
                .vipExpiryDate(null)
                .vipLevel(null)
                .build();

        userRepository.save(user);

        companyRepository.save(Company.builder()
                .companyName(registrationRequest.getCompanyName())
                .industry(registrationRequest.getIndustryIds()
                        .stream()
                        .map(industryId -> Industry.builder().industryId(industryId).build())
                        .collect(Collectors.toList()))
                .logoPath(logoPath)
                .website(registrationRequest.getWebsite())
                .description(registrationRequest.getDescription())
                .isVerified(true)
                .user(user)
                .createJobCount(5)
                .build());

        pendingRegistrations.remove(request.getEmail());
        pendingLogoPaths.remove(request.getEmail());
        pendingOtps.remove(request.getEmail());
        pendingOtpExpiries.remove(request.getEmail());
    }

    @Override
    public void resendOtp(String email) {
        if (!pendingRegistrations.containsKey(email)) {
            throw new BadRequestException("No registration found for this email");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime otpExpiry = LocalDateTime.now().plusMinutes(10);

        pendingOtps.put(email, otp);
        pendingOtpExpiries.put(email, otpExpiry);

        try {
            emailConfig.sendOtpEmail(email, otp);
        } catch (Exception e) {
            throw new BadRequestException("Failed to send OTP email: " + e.getMessage());
        }
    }
}
