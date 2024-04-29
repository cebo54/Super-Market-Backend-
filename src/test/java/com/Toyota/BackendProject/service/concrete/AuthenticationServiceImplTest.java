package com.Toyota.BackendProject.service.concrete;

import com.Toyota.BackendProject.Dao.UserRepository;
import com.Toyota.BackendProject.Entity.User;
import com.Toyota.BackendProject.dto.request.LoginDto;
import com.Toyota.BackendProject.dto.response.UserResponse;
import com.Toyota.BackendProject.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationServiceImplTest {

    private UserRepository userRepository;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        userRepository= Mockito.mock(UserRepository.class);
        jwtService=Mockito.mock(JwtService.class);
        authenticationManager=Mockito.mock(AuthenticationManager.class);
        authenticationService=new AuthenticationServiceImpl(userRepository,jwtService,authenticationManager);
    }

    @Test
    void shouldProvideLoginToUser_ReturnsToken() {
        User user=new User();
        user.setUsername("username");
        user.setPassword("password");

        LoginDto loginDto=new LoginDto();
        loginDto.setUsername(user.getUsername());
        loginDto.setPassword(user.getPassword());

        when(userRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("testToken");

        UserResponse userResponse=authenticationService.login(loginDto);

        assertNotNull(userResponse.getToken());
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("username", "password"));
        verify(userRepository).findByUsername("username");
        verify(jwtService).generateToken(user);
    }

    @AfterEach
    void tearDown() {
    }
}

