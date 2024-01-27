package com.Toyota.BackendProject.service.Abstract;

import com.Toyota.BackendProject.dto.LoginDto;
import com.Toyota.BackendProject.dto.RegisterDto;
import com.Toyota.BackendProject.dto.UserResponse;

public interface AuthenticationService {
    UserResponse save(RegisterDto registerDto);

    UserResponse login(LoginDto loginDto);
}
