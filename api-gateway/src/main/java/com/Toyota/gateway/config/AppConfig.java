package com.Toyota.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

//Configuration class
// @Configuration indicates that the class has @Bean definition methods
@Configuration
public class AppConfig {
    /**
    * Defining a bean for making HTTP requests
    * @Bean to specify that it returns a bean to be managed by Spring context
    * @LoadBalanced creates an instance of created RestTemplate load-balanced
    */
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {

        return WebClient.builder();
    }
}