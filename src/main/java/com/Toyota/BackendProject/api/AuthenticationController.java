package com.Toyota.BackendProject.api;


import com.Toyota.BackendProject.Util.GenericResponse;
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

    @PostMapping("/login")
    public GenericResponse<UserResponse> login(@RequestBody LoginDto loginDto){
        return GenericResponse.successResult(authenticationService.login(loginDto),"success.message.successful");

    }

}
