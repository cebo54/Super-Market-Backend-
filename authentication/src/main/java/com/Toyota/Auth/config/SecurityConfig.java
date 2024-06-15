package com.Toyota.Auth.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// This class defines the security configuration for the application, specifying parameters for login,
// permissions for URLs, and specifying which URLs should be accessible without authentication (permitAll).
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    private final AuthenticationProvider authenticationProvider;

    /**
     * Bean to configure the security filter chain.
     *
     * @param http HttpSecurity object to configure security settings
     * @return SecurityFilterChain configured with authentication and authorization rules
     * @throws Exception If configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.csrf().disable()// Disable CSRF protection
                .authorizeHttpRequests() // determine the authorization rules for HTTP requests
                .antMatchers("/auth/**")  // Allow all requests to authentication endpoints
                .permitAll()
                .antMatchers("/product/**") // Allow all requests to product endpoints
                .permitAll()
                .antMatchers("/category/**")// Allow all requests to product endpoints
                .permitAll()
                .antMatchers("/campaign/**")// Allow all requests to product endpoints
                .permitAll()
                .antMatchers("/sale/**")// Restrict access to sale endpoints to users with role 'CASHIER'
                .hasRole("CASHIER")
                .antMatchers("/user/**")// Restrict access to user endpoints to users with role 'ADMIN'
                .hasRole("ADMIN")
                .antMatchers("/report/**")// Restrict access to report endpoints to users with role 'STOREMANAGER'
                .hasRole("STOREMANAGER")
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add the JWT authentication filter before the UsernamePasswordAuthenticationFilter
        return http.build();

    }
}
