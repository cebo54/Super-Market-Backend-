package com.Toyota.BackendProject.service.concrete;

import com.Toyota.BackendProject.Dao.RoleRepository;
import com.Toyota.BackendProject.Dao.UserRepository;
import com.Toyota.BackendProject.Entity.Role;
import com.Toyota.BackendProject.Entity.User;
import com.Toyota.BackendProject.dto.LoginDto;
import com.Toyota.BackendProject.dto.RegisterDto;
import com.Toyota.BackendProject.dto.UserResponse;
import com.Toyota.BackendProject.security.JwtService;
import com.Toyota.BackendProject.service.Abstract.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    public UserResponse save(RegisterDto registerDto) {
        List <Role> roles = registerDto.getRole_id().stream().map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        User user= User.builder().e_mail(registerDto.getE_mail())
                .name(registerDto.getName())
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .roles(roles).build();

        userRepository.save(user);

        var token=jwtService.generateToken(user);

        return UserResponse.builder().token(token).build();

    }

    @Override
    public UserResponse login(LoginDto loginDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(),loginDto.getPassword()));
        User user= userRepository.findByUsername(loginDto.getUsername()).orElseThrow();
        String token= jwtService.generateToken(user);
        return UserResponse.builder().token(token).build();
    }
}
