package com.example.appointmentservice.security;


import java.util.Arrays;
import org.springframework.web.cors.CorsConfigurationSource;


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
    public SecurityFilterChain securityFilterChain(HttpSecurity http,CorsConfigurationSource corsConfigurationSource) throws Exception {

        return http.
        		cors(cors->cors.configurationSource(corsConfigurationSource))
        		.csrf(customizer -> customizer.disable()).
                        authorizeHttpRequests(request -> request

                		// All endpoints from AppointmentController
                        .requestMatchers(
                                "/api/appointments/getAll",
                                "/api/appointments/{appointmentId}",
                                "/api/appointments/add",
                                "/api/appointments/update/{appointmentId}",
                                "/api/appointments/delete/{appointmentId}",
                                "/api/appointments/deleteAll",
                                "/api/appointments/current/patient/{patientId}",
                                "/api/appointments/current/doctor/{doctorId}",
                                "/api/appointments/past/patient/{patientId}",
                                "/api/appointments/past/doctor/{doctorId}",
                                "/api/appointments/book",
                                "/api/appointments/reschedule",
                                "/api/appointments/cancel/{appointmentId}",
                                "/api/appointments/date/{appointmentDate}",
                                "/api/appointments/completeAppointment/{appointmentId}",
                                "/api/appointments/patient/{patientId}",
                                "/api/appointments/doctor/{doctorId}",
                                "/api/appointments/filter/{startDate}/{endDate}"
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




