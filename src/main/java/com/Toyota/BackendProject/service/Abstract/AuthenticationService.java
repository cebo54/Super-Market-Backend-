package com.Toyota.BackendProject.service.Abstract;

import com.Toyota.BackendProject.dto.request.LoginDto;
import com.Toyota.BackendProject.dto.request.RegisterDto;
import com.Toyota.BackendProject.dto.response.UserResponse;

public interface AuthenticationService {
    UserResponse save(RegisterDto registerDto);

    UserResponse login(LoginDto loginDto);
}
