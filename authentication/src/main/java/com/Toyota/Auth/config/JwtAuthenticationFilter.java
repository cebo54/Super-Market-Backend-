package com.Toyota.Auth.config;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.Toyota.Auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Filter class for JWT authentication.
 * Intercepts incoming HTTP requests, extracts JWT tokens, validates them, and sets up Spring Security authentication.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    Logger logger = Logger.getLogger(JwtAuthenticationFilter.class);

    private final UserDetailsService userDetailsService;

    /**
     * Method to filter incoming HTTP requests and apply JWT authentication.
     *
     * @param request     HTTP servlet request
     * @param response    HTTP servlet response
     * @param filterChain Filter chain to continue processing the request
     * @throws ServletException If there is a servlet exception
     * @throws IOException      If there is an IO exception
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Check if the request contains the Authorization header with Bearer token
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from the header
        jwt = header.substring(7);
        // Retrieve username from the JWT token
        username = jwtService.findUsername(jwt);

        // Validate the token and set the authentication in the security context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.tokenControl(jwt, userDetails)) {
                // Create authentication token and set it in the security context
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                logger.info("auth failed");
            }
        }
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}