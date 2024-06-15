package com.Toyota.Auth.config;


import com.Toyota.Auth.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for application-specific security settings.
 */

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;

    /**
     * Provides a UserDetailsService bean implementation.
     * UserDetailsService is used by Spring Security to load user-specific data during authentication.
     *
     * @return UserDetailsService implementation that loads user details from UserRepository.
     */
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User Not Found"));
    }

    /**
     * Provides an AuthenticationProvider bean.
     * DaoAuthenticationProvider is used to authenticate users based on UserDetailsService and verify passwords.
     *
     * @return AuthenticationProvider bean configured with UserDetailsService and password encoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        // Setting the UserDetailsService to provide user-specific data
        authenticationProvider.setUserDetailsService(userDetailsService());
        // Setting the password encoder to verify passwords
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Provides a PasswordEncoder bean.
     * BCryptPasswordEncoder is used to encode passwords securely.
     *
     * @return PasswordEncoder bean instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    /**
     * Provides an AuthenticationManager bean.
     * AuthenticationManager is used by Spring Security to process authentication requests.
     *
     * @param authenticationConfiguration AuthenticationConfiguration to obtain the AuthenticationManager.
     * @return AuthenticationManager bean instance.
     * @throws Exception If there is an error obtaining the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)throws Exception{
        // AuthenticationManager is used to process authentication requests
        return authenticationConfiguration.getAuthenticationManager();
    }


}
