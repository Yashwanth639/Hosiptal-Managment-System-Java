package com.example.appointmentservice.security;


import java.io.IOException;
import java.util.List;

//import org.apache.catalina.realm.JNDIRealm.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
/**
 * The JwtFilter class extends OncePerRequestFilter to filter incoming HTTP requests
 * and validate JWT tokens. It sets the authentication context if the token is valid.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Debug: Log incoming request details
        System.out.println("JwtFilter invoked for request URL: " + request.getRequestURL());
        System.out.println("Request Method: " + request.getMethod());

        // Get Authorization header
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Authorization header is missing or does not start with 'Bearer '");
            chain.doFilter(request, response); // Continue the filter chain
            return;
        }

        // Extract token from Authorization header
        String token = authHeader.substring(7);
        System.out.println("Extracted Token: " + token);

        // Validate the token
        boolean isValidToken = jwtUtil.validateToken(token);
        System.out.println("Is Token Valid: " + isValidToken);

        if (!isValidToken) {
            System.out.println("Token validation failed");
            chain.doFilter(request, response); // Continue the filter chain
            return;
        }

     // Extract details from token
        String username = jwtUtil.extractEmail(token);
        String roles = jwtUtil.extractRoles(token);
        String userId = jwtUtil.extractUserId(token); // Extract userId from the JWT token
        System.out.println("Extracted Username: " + username);
        System.out.println("Extracted Roles: " + roles);
        System.out.println("Extracted User ID: " + userId);

        // Set userId as a request attribute
        request.setAttribute("userId", userId);
        System.out.println("User ID added to request attributes: " + userId);

        // Convert roles to GrantedAuthority list
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roles));
        System.out.println("Granted Authorities: " + authorities);

        // Create UserDetails object
        UserDetails userDetails = new User(username, "N/A", authorities);
        System.out.println("UserDetails Created: " + userDetails);

        // Set authentication in SecurityContext
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        System.out.println("Authentication set in SecurityContext for user: " + username);

        // Continue the filter chain
        chain.doFilter(request, response);
    }
}
