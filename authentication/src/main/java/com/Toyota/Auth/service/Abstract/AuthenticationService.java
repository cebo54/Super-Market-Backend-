package com.Toyota.Auth.service.Abstract;


import com.Toyota.Auth.dto.request.LoginDto;
import com.Toyota.Auth.dto.response.UserResponse;

public interface AuthenticationService {

    UserResponse login(LoginDto loginDto);

    void validateToken(String token);
}
