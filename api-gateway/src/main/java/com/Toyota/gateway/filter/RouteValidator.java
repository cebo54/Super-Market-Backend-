package com.Toyota.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

// @Component allows Spring to detect our custom beans automatically
@Component
public class RouteValidator {
    // List of endpoints that are don't require authentication
    public static final List<String> openApiEndpoints = List.of(
            "/auth/**",
            "/eureka/**"


    );

    // Predicate to check if the request is for a secured endpoint
    public Predicate<ServerHttpRequest> isSecured  =
            serverHttpRequest -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));

}
