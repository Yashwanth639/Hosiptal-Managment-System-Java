package com.example.loginservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignClientConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String token = null;

            // Attempt to retrieve the token from SecurityContext
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
                System.out.println("Feign Interceptor - Token from SecurityContext: " + token);
            }

            // Use a predefined service token if SecurityContext is empty
            if (token == null || token.isEmpty()) {
                token = "your-service-account-token"; // Replace this with a valid service token or dynamically generate one
                System.out.println("Feign Interceptor - Using Service Account Token");
            }

            // Add the token to the Authorization header
            if (token != null && !token.isEmpty()) {
                requestTemplate.header("Authorization", "Bearer " + token);
                System.out.println("Authorization Header Set: Bearer " + token);
            } else {
                System.out.println("Authorization Header Missing");
            }
        };
    }
}

