package com.Toyota.BackendProject.api;


import com.Toyota.BackendProject.dto.request.LoginDto;
import com.Toyota.BackendProject.dto.request.RegisterDto;
import com.Toyota.BackendProject.dto.response.UserResponse;
import com.Toyota.BackendProject.service.Abstract.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private AuthenticationService authenticationService;
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    @PostMapping("/save")
    public ResponseEntity<UserResponse>save(@RequestBody RegisterDto registerDto){
        return ResponseEntity.ok(authenticationService.save(registerDto));

    }
    @PostMapping("/login")
    public ResponseEntity<UserResponse>login(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok(authenticationService.login(loginDto));

    }

}
