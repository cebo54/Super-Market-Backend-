package com.Toyota.gateway.filter;

import com.Toyota.gateway.util.GenericResponse;
import com.Toyota.gateway.util.JwtUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Custom Gateway Filter for authentication and authorization using JWT tokens.
 * This filter is responsible for intercepting incoming requests, validating JWT tokens,
 * and enforcing access control based on user roles.
 */

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class);

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WebClient.Builder webClient;

    public AuthenticationFilter() {
        super(Config.class);
    }


     /**
      * * apply method is responsible for handling the validation of authentication tokens.
     *  @param config is used to carry configuration information in Spring Cloud Gateway filters.
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            //Checks if the request is secured
            if (validator.isSecured.test(exchange.getRequest())) {
                //Checks for Authorization Header
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    logger.info("HTTP request does not contain authorization header.");
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                }

                //Extract Token from Authorization Header
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                String finalAuthHeader = authHeader;
                return webClient.build().get()
                        .uri("http://authentication-service/auth/validate/" + authHeader)
                        .retrieve()
                        .bodyToMono(GenericResponse.class)
                        .flatMap(response -> {
                            // Log response details
                            logger.info("Token validation response: " + response);
                            // Handle Response from Authentication Service
                            if (response == null || !"Token is valid.".equals(response.getMessage())) {
                                logger.warn(String.format("According to the token-service response, token couldn't be validated. username: %s", jwtUtil.getUsernameFromToken(finalAuthHeader)));
                                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                            }

                            //Extract User Roles and Username from Token
                            List<String> roles = jwtUtil.parseTokenGetRoles(finalAuthHeader);
                            String username = jwtUtil.getUsernameFromToken(finalAuthHeader);

                            // Log user roles and username
                            logger.info(String.format("Username: %s, Roles: %s", username, roles));

                            String path = exchange.getRequest().getURI().getPath();

                            // ~/product/** endpoints does not need role to access
                            // user who has any role can access this point.
                            if (path.startsWith("/auth/") || path.startsWith("/product/")) {
                                if (roles.isEmpty()) {
                                    logger.fatal(String.format("user: %s does not have role. ", username));
                                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                                }

                                // ~/user/** endpoints needs admin role to access
                            } else if (path.startsWith("/user/")) {
                                if (!roles.contains("ROLE_ADMIN")) {
                                    logger.info(String.format("user: %s has no admin role to access /user/** endpoint", username));
                                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                                }

                                // ~/sale/** endpoints needs admin role to access
                            } else if (path.startsWith("/sale/")) {
                                if (!roles.contains("ROLE_CASHIER")) {
                                    logger.info(String.format("user: %s has no cashier role to access /sale/** endpoint", username));
                                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                                }
                                // ~/report/** endpoints needs admin role to access
                            } else if (path.startsWith("/report/")) {
                                if (!roles.contains("ROLE_STOREMANAGER")) {
                                    logger.info(String.format("user: %s has no store manager role to access /report/** endpoint", username));
                                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                                }
                                // throw an exception when trying to access invalid endpoint
                            } else {
                                logger.debug("Invalid endpoint access");
                                return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
                            }

                            return Mono.empty();
                        }).then(chain.filter(exchange));
            }

            return chain.filter(exchange);
        };
    }

    /**
     * Default constructor initializing the AuthenticationFilter with its configuration class.
     */
    public static class Config {
    }
}
