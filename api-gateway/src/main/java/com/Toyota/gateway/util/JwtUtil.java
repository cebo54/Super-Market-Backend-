package com.Toyota.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;
import java.util.function.Function;
/*
* The class provides the methods necessary to parse JWTs and obtain the information within them
*/

@Component
public class JwtUtil {
    private final static Logger logger = Logger.getLogger(JwtUtil.class);

    @Value("${security.jwt.secret}")
    private String SECRET_KEY;

    /** Retrieves the username from the token
     * @param token is JWT token
     */
    public String getUsernameFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getSubject);
        } catch (Exception UsernameNotFoundException) {
            logger.warn("Invalid username while getting from token");
            throw UsernameNotFoundException;
        }
    }

    /** Retrieves a claim from the JWT token
    *  @param token is JWT token
    *  @param claimsResolver is used to extract different information (claims) in the token.
    */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.warn("Claims couldn't get from the token with function", e);
            throw e;
        }
    }
    /** With parsing JWT token retrieves all claims
    *  @param token is JWT token
    */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.warn("Couldn't get the information from token with secret key", e);
            throw e;
        }
    }

    /** With parsing JWT token returns a list of roles
    *  @param token is JWT token
    */
    public List<String> parseTokenGetRoles(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            List<String> roles = claims.get("roles", List.class);
            if (roles == null || roles.isEmpty()) {
                logger.warn("Roles are null or empty in the token");
                throw new IllegalArgumentException("Roles are not present in the token");
            }
            return roles;
        } catch (Exception e) {
            logger.warn("Could not parse roles from token", e);
            throw e;
        }
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}