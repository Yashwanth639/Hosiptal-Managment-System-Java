package com.cts.medicalhistoryservice.security;


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

	/**
     * Filters each request to check for a valid JWT token and sets the authentication context if the token is valid.
     *
     request.
     * @param response The HTTP response.
     * @param chain    The filter chain.
     * @throws ServletException If an error occurs during filtering.
     * @throws IOException      If an I/O error occurs during filtering.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
    	
 
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
 
        String token = authHeader.substring(7);
        System.out.println(token);
        System.out.print(">>>>>>>>>" + jwtUtil.validateToken(token));

        if (!jwtUtil.validateToken(token)) {
            chain.doFilter(request, response);
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
//        // Extract user details from JWT
//        String username = jwtUtil.extractEmail(token);
//        String roles = jwtUtil.extractRoles(token);
//        
//        System.out.print(username);
// 
        // Convert roles to GrantedAuthority list
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roles));
 
        // Create UserDetails object
 
        UserDetails userDetails = new User(username, "N/A", authorities);
        System.out.println("test:"+userDetails);
        // Set authentication in security context
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
 
        SecurityContextHolder.getContext().setAuthentication(authToken);
 
        chain.doFilter(request, response);
    }
}
 
