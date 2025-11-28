package com.example.loginservice.config;

import com.example.loginservice.filter.JwtFilter;
import com.example.loginservice.service.CustomUserDetailsService;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for Spring Security. Configures authentication and
 * authorization for the application.
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Lazy
	@Autowired
	JwtFilter jwtFilter;

	@Lazy
	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * Creates a PasswordEncoder bean for encoding passwords.
	 *
	 * @return BCryptPasswordEncoder instance.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Configures the security filter chain.
	 *
	 * @param http HttpSecurity object.
	 * @return SecurityFilterChain instance.
	 * @throws Exception If an error occurs during configuration.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource)
			throws Exception {
		return http.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.csrf(customizer -> customizer.disable())
				.authorizeHttpRequests(request -> request
						.requestMatchers("/api/users/register", "/api/users/login", "/api/users/register/patient",
								"/api/users/register/doctor")
						.permitAll()
						.requestMatchers("/api/patients/getAll", "/api/patients/{patientId}",
								"/api/patients/name/{pName}", "/api/patients/update",
								"/api/patients/delete/{patientId}", "/api/patients/deleteAll",
								"/api/patients/current/patient/{patientId}", "/api/patients/past/patient/{patientId}",
								"/api/patients/medicalHistory/{patientId}", "/api/patients/appointment/{patientId}",
								"/api/patients/availableDoctors/{specializationName}/{availableDate}/{session}",
								"/api/patients/bookAppointment", "/api/patients/rescheduleAppointment",
								"/api/patients/cancelAppointment/{appointmentId}",
								"/api/patients/notifications/{patientId}",
								"/api/patients/notifications/markAsRead/{notificationId}",
								"/api/patients/filterAppointmentsByDate/{startDate}/{endDate}/{patientId}/{appointmentStatus}")
						.authenticated().anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	/**
	 * Creates an AuthenticationProvider bean.
	 *
	 * @return DaoAuthenticationProvider instance.
	 */
	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}

	/**
	 * Creates an AuthenticationManager bean.
	 *
	 * @param config AuthenticationConfiguration object.
	 * @return AuthenticationManager instance.
	 * @throws Exception If an error occurs during configuration.
	 */
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
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
