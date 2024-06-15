package com.Toyota.Auth.service.concrete;


import com.Toyota.Auth.dao.UserRepository;
import com.Toyota.Auth.dto.request.LoginDto;
import com.Toyota.Auth.dto.response.UserResponse;
import com.Toyota.Auth.entity.Role;
import com.Toyota.Auth.entity.User;
import com.Toyota.Auth.security.JwtService;
import com.Toyota.Auth.service.Abstract.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implementation of AuthenticationService for user authentication and token management.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger = Logger.getLogger(AuthenticationServiceImpl.class);

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    /**
     * Authenticate a user based on the provided login credentials.
     *
     * @param loginDto The DTO containing username and password for authentication.
     * @return A UserResponse containing the generated JWT token upon successful authentication.
     * @throws AuthenticationException If authentication fails due to incorrect credentials.
     */
    @Override
    public UserResponse login(LoginDto loginDto) {
        try {
            //get user by username
            User user = userRepository.findByUsername(loginDto.getUsername())
                    .orElseThrow(
                            ()-> new NullPointerException(String.format("username not found : %s",loginDto.getUsername())));
            //authenticate user
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (loginDto.getUsername(), loginDto.getPassword()));
            //get user role list.
            List <String> roles = user.getRoles().stream().map(Role::getName).toList();
            //generate token
            String token = jwtService.generateToken(user,roles);

            logger.info(String.format("User: %s is authenticated",user.getUsername()));
            return UserResponse.builder().token(token).build();
        }
        catch (AuthenticationException e){
            logger.debug(String.format("Password is not correct. username: %s",loginDto.getUsername()));
            throw e;
        }
    }

    /**
     * Validate the authenticity and expiration of a given JWT token.
     *
     * @param token The JWT token to validate.
     */
    @Override
    public void validateToken(String token) {

        jwtService.validateToken(token);
    }
}
