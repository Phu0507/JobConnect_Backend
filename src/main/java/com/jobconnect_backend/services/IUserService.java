package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.UserDTO;
import com.jobconnect_backend.dto.request.UpdatePersonalInfoRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
public interface IUserService {
    List<UserDTO> getAllUsers();
//    void updateProfileInfo(UpdatePersonalInfoRequest request, BindingResult bindingResult);
}
