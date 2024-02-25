package com.Toyota.BackendProject.config;
//hangi parametrelerle giriş yapılabilmesi,hangi urllere izin verilip verilmediğini ,hangilerine permitAll dememiz gerektiğini belirten sınıf

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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.csrf().disable()
                .authorizeHttpRequests()
                .antMatchers("/auth/**")
                .permitAll()
                .antMatchers("/product/**")
                .permitAll()
                .antMatchers("/category/**")
                .permitAll()
                .antMatchers("/campaign/**")
                .permitAll()
                .antMatchers("/sale/**")
                .hasRole("CASHIER")
                .antMatchers("/user/**")
                .hasRole("ADMIN")
                .antMatchers("/report/**")
                .hasRole("STOREMANAGER")
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }
}
