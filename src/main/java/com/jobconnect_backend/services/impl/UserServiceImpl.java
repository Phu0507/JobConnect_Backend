package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.config.AwsS3Service;
import com.jobconnect_backend.dto.dto.UserDTO;
import com.jobconnect_backend.dto.request.UpdatePersonalInfoRequest;
import com.jobconnect_backend.entities.Company;
import com.jobconnect_backend.entities.Industry;
import com.jobconnect_backend.entities.JobSeekerProfile;
import com.jobconnect_backend.entities.User;
import com.jobconnect_backend.entities.enums.Role;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.CompanyRepository;
import com.jobconnect_backend.repositories.IndustryRepository;
import com.jobconnect_backend.repositories.JobSeekerProfileRepository;
import com.jobconnect_backend.repositories.UserRepository;
import com.jobconnect_backend.services.IUserService;
import com.jobconnect_backend.utils.ValidateField;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final String CHANGE_TYPE_UPDATE = "UPDATE";
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final IndustryRepository industryRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidateField validateField;
    private final AwsS3Service awsS3Service;

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = UserDTO.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .role(user.getRole().name())
                    .build();
            userDTOs.add(userDTO);
        }
        return userDTOs;
    }

    @Override
    public void updateProfileInfo(UpdatePersonalInfoRequest request, BindingResult bindingResult) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        Map<String, String> errors = validateField.getErrors(bindingResult);
        if (!errors.isEmpty()) {
            throw new BadRequestException("Validation errors", errors);
        }

        user.setPhone(request.getPhoneNumber());

        String logoPath = uploadFile(request.getLogoPath(), "logo");
        String avatarPath = uploadFile(request.getAvatar(), "avatar");

        if (Role.COMPANY.equals(user.getRole())) {
            Company company = companyRepository.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new BadRequestException("Company not found with this user id"));

            validateField.getCompanyFieldErrors(errors, request.getCompanyName());

            company.setCompanyName(request.getCompanyName());
            company.setWebsite(request.getWebsite());
            company.setDescription(request.getDescription());

            if (logoPath != null) {
                company.setLogoPath(logoPath);
            }

            if (request.getIndustryIds() != null && !request.getIndustryIds().isEmpty()) {
                List<Industry> industries = industryRepository.findAllById(request.getIndustryIds());
                company.setIndustry(industries);
            } else {
                company.setIndustry(new ArrayList<>());
            }

            companyRepository.save(company);

        } else if (Role.JOBSEEKER.equals(user.getRole())) {
            JobSeekerProfile profile = jobSeekerProfileRepository.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new BadRequestException("JobSeekerProfile not found with this user id"));

            validateField.getJobSeekerFieldErrors(errors, request.getFirstName(), request.getLastName());

            profile.setFirstName(request.getFirstName());
            profile.setLastName(request.getLastName());
            profile.setTitle(request.getTitle());
            profile.setBirthDay(request.getBirthDay());
            profile.setAddress(request.getAddress());

            if (avatarPath != null) {
                profile.setAvatar(avatarPath);
            }

            jobSeekerProfileRepository.save(profile);
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Please complete all required fields to proceed.", errors);
        }

        userRepository.save(user);
    }

    private String uploadFile(MultipartFile file, String fileType) {
        if (file != null && !file.isEmpty()) {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || !originalFileName.contains(".")) {
                throw new BadRequestException("Invalid " + fileType + " file name.");
            }

            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
            String s3Key = baseName + "_" + System.currentTimeMillis() + extension;

            try {
                return awsS3Service.uploadFileToS3(file.getInputStream(), s3Key, file.getContentType());
            } catch (IOException e) {
                throw new BadRequestException("Failed to upload " + fileType + ".");
            }
        }
        return null;
    }
}
