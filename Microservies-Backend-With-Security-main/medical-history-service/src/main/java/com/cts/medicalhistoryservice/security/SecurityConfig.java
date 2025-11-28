package com.cts.medicalhistoryservice.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * The SecurityConfig class configures the security settings for the application.
 * It defines the security filter chain and specifies the authorization rules for different endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    /**
     * Configures the security filter chain.
     *
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {

        return http.
        		cors(cors->cors.configurationSource(corsConfigurationSource))
        		.csrf(customizer -> customizer.disable()).
                authorizeHttpRequests(request -> request

                		 // All endpoints from MedicalHistoryController
                        .requestMatchers(
                                "/medical-history/getAll",
                                "/medical-history/{medicalHistoryId}",
                                "/medical-history/update/{mhId}",
                                "/medical-history/delete/{mhId}",
                                "/medical-history/deleteAll",
                                "/medical-history/patient/{patientId}",
                                "/medical-history/doctor/{doctorId}",
                                "/medical-history/patient/{patientId}/date",
                                "/medical-history/add",
                                "/medical-history/between/{doctorId}/{patientId}",
                                "/medical-history/filterByDate/{startDate}/{endDate}/{patientId}"
                        ).authenticated()
                            .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // Allow frontend requests
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow HTTP methods
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept")); // Define headers
        configuration.setAllowCredentials(true); // Allow cookies/session headers
 
        // Register configuration
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}



