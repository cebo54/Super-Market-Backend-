package com.Toyota.Auth.api;

import com.Toyota.Auth.dto.request.LoginDto;
import com.Toyota.Auth.dto.response.UserResponse;
import com.Toyota.Auth.service.Abstract.AuthenticationService;
import com.Toyota.Auth.util.GenericResponse;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private static final Logger logger = Logger.getLogger(AuthenticationController.class);

    private AuthenticationService authenticationService;
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
    * This request provides the login for user
    * @param loginDto is dto which includes login info's
    */
    @PostMapping("/login")
    public GenericResponse<UserResponse> login(@RequestBody LoginDto loginDto){
        logger.info("Received login request for user: " + loginDto.getUsername());
        // Authenticate the user and return a successful response with user details
        try {
            UserResponse userResponse = authenticationService.login(loginDto);
            logger.info("Login successful for user: " + loginDto.getUsername());
            return GenericResponse.successResult(userResponse, "success.message.successful");
        } catch (Exception e) {
            logger.error("Login failed for user: " + loginDto.getUsername(), e);
            return GenericResponse.errorResult("success.message.error");
        }
    }

    /** Validate the token and return a success message if valid
    *  @param token is JWT token
    */
    @GetMapping("/validate/{token}")
    public GenericResponse<String> validateToken(@PathVariable("token")String token){
        logger.info("Received token validation request for token: " + token);
        try {
            authenticationService.validateToken(token);
            logger.info("Token validation successful for token: " + token);
            return GenericResponse.success("success.message.vtoken");
        } catch (Exception e) {
            logger.error("Token validation failed for token: " + token, e);
            return GenericResponse.errorResult("success.message.error");
        }
    }
}

