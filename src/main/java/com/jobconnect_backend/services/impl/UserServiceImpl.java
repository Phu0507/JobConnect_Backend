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


}
