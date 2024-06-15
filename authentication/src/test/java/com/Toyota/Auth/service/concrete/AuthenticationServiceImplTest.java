package com.Toyota.Auth.service.concrete;

import com.Toyota.Auth.dao.UserRepository;
import com.Toyota.Auth.entity.Role;
import com.Toyota.Auth.entity.User;
import com.Toyota.Auth.dto.request.LoginDto;
import com.Toyota.Auth.dto.response.UserResponse;
import com.Toyota.Auth.security.JwtService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import javax.crypto.SecretKey;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class AuthenticationServiceImplTest {

    private UserRepository userRepository;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private AuthenticationServiceImpl authenticationService;

    private Logger logger;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        userRepository= Mockito.mock(UserRepository.class);
        jwtService=Mockito.mock(JwtService.class);
        authenticationManager=Mockito.mock(AuthenticationManager.class);
        secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        logger=Mockito.mock(Logger.class);
        when(jwtService.getKey()).thenReturn(secretKey);
        authenticationService=new AuthenticationServiceImpl(userRepository,jwtService,authenticationManager);
    }


    private User createUser() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRoles(Collections.singletonList(role));
        return user;
    }

    @Test
    void shouldReturnJwtToken_whenUserLoginSuccessfully() {
        User user = createUser();
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("username");
        loginDto.setPassword("password");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtService.generateToken(any(User.class), anyList())).thenReturn("mockToken");

        UserResponse response = authenticationService.login(loginDto);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        verify(userRepository).findByUsername("username");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(any(User.class), anyList());
    }

    @Test
    void shouldUserCantLogin_withInvalidPassword() {
        User user = createUser();
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("username");
        loginDto.setPassword("password");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authenticationService.login(loginDto);
        });

        assertTrue(exception instanceof BadCredentialsException);
        verify(userRepository).findByUsername("username");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("username");
        loginDto.setPassword("password");

        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            authenticationService.login(loginDto);
        });

        assertEquals("username not found : username", exception.getMessage());
        verify(userRepository).findByUsername("username");
    }

    @Test
    void shouldReturnSuccessMessage_whenTokenIsValid() {
        doNothing().when(jwtService).validateToken("validToken");

        assertDoesNotThrow(() -> {
            authenticationService.validateToken("validToken");
        });

        verify(jwtService).validateToken("validToken");
    }

    @Test
    void shouldReturnException_whenTokenIsInvalid() {
        doThrow(new RuntimeException("Invalid token")).when(jwtService).validateToken("invalidToken");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.validateToken("invalidToken");
        });

        assertEquals("Invalid token", exception.getMessage());
        verify(jwtService).validateToken("invalidToken");
    }

    @AfterEach
    void tearDown() {
    }
}

