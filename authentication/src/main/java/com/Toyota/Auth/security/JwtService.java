package com.Toyota.Auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// Service class for generating and validating JWT tokens
@Service
public class JwtService {
    private static final Logger logger = Logger.getLogger(JwtService.class);

    // Injected value for the JWT secret key from application properties
    @Value("${security.jwt.secret}")
    private String SECRET_KEY;

    // Injected value for the JWT expiration time in seconds from application properties
    @Value("${security.jwt.expire}")
    private Long Expire;

    // Method to extract the username from a given JWT token
    public String findUsername(String token) {

        return exportToken(token, Claims::getSubject);
    }

    // Generic method to parse the token and apply a given function to the claims
    private <T> T exportToken(String token, Function<Claims,T>claimsTFunction) {
        final Claims claims= Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build().parseClaimsJws(token).getBody();

        return claimsTFunction.apply(claims);
    }

    // Method to decode the base64-encoded secret key and return it as a Key object
    public Key getKey() {
        byte[] key= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }
    // Method to validate the token by checking the username and expiration date
    public boolean tokenControl(String jwt, UserDetails userDetails) {
        // Extract the username from the token
        final String username=findUsername(jwt);
        // Validate the token by checking the username and ensuring it is not expired
        return(username.equals(userDetails.getUsername()) && !exportToken(jwt,Claims::getExpiration).before(new Date()));
    }

    // Method to generate a JWT token for the given user details and roles
    public String generateToken(UserDetails userDetails, List<String> roles) {
        try {
            // Create a map of claims to store roles
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles",roles);
            // Generate the token with the claims and the username
            return doGenerateToken(claims, userDetails.getUsername());
        } catch (Exception e) {
            // Log a warning if token generation fails
            logger.warn(String.format("Couldn't generate token with user details, username: %s, roles: %s",userDetails.getUsername(),roles));
            throw e;
        }
    }
    // Method to generate the token using the claims and subject (username)
    private String doGenerateToken(Map<String, Object> claims, String subject) {

        try {
            // Build and return the JWT token
            return Jwts.builder()
                    .setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + Expire * 1000))
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
        } catch (Exception e) {
            logger.warn(String.format("couldn't do generate token username:%s ", subject));
            throw e;
        }
    }
    // Method to validate the token by parsing it
    public void validateToken(final String token){
        Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
    }
}
