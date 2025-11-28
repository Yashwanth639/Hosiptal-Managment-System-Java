package com.example.loginservice.filter;


import com.example.loginservice.service.CustomUserDetailsService;
import com.example.loginservice.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter for handling JWT authentication.
 * This filter intercepts incoming requests, extracts the JWT token from the Authorization header,
 * validates the token, and sets the authentication context for subsequent requests.
 *
 */

@Component
public class JwtFilter extends OncePerRequestFilter {
  
  @Autowired
  private JWTService jwtService;

  @Autowired
  private CustomUserDetailsService userDetailsService;
  @Override
  protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain)
          throws ServletException, IOException {
      
      // Log request details
      System.out.println("JwtFilter invoked for request URL: " + request.getRequestURL());
      System.out.println("Request Method: " + request.getMethod());

      // Extract Authorization header
      String authHeader = request.getHeader("Authorization");
      System.out.println("Authorization Header: " + authHeader);

      String token = null;
      String email = null;

      // Extract token and email from Authorization header
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
          token = authHeader.substring(7);
          email = jwtService.extractEmail(token);
          System.out.println("Extracted Token: " + token);
          System.out.println("Extracted Email: " + email);
      }

      // Validate email and set SecurityContext if not already set
      if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(email);
          System.out.println("Loaded UserDetails for Email: " + email);

          if (jwtService.validateToken(token, userDetails)) {
              System.out.println("Token Validation Successful");

              // Extract role and userId from token
              String role = jwtService.extractRole(token);
              String userId = jwtService.extractUserId(token); // Assuming this method exists in jwtService
              System.out.println("Extracted Role: " + role);
              System.out.println("Extracted User ID: " + userId);

              // Add userId to request attributes
              request.setAttribute("userId", userId);
              System.out.println("User ID added to request attributes: " + userId);

              // Create authorities based on the role
              List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
              System.out.println("Granted Authorities: " + authorities);

              // Create authentication token
              UsernamePasswordAuthenticationToken authToken =
                      new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
              authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
              System.out.println("Authentication Token Created: " + authToken);

              // Set authentication in SecurityContext
              SecurityContextHolder.getContext().setAuthentication(authToken);
              System.out.println("SecurityContext Updated with Authentication Token");
          } else {
              System.out.println("Token Validation Failed");
          }
      } else if (email != null) {
          System.out.println("Authentication Already Set in SecurityContext");
      }

      // Continue the filter chain
      filterChain.doFilter(request, response);
  }
}


//@Component
//public class JwtFilter extends OncePerRequestFilter {
//    
//    @Autowired
//    private JWTService jwtService;
//
//    @Autowired
//    private CustomUserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, 
//                                    HttpServletResponse response, 
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//        
//        // Log request details
//        System.out.println("JwtFilter invoked for request URL: " + request.getRequestURL());
//        System.out.println("Request Method: " + request.getMethod());
//
//        // Extract Authorization header
//        String authHeader = request.getHeader("Authorization");
//        System.out.println("Authorization Header: " + authHeader);
//
//        String token = null;
//        String email = null;
//
//        // Extract token and email from Authorization header
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            email = jwtService.extractEmail(token);
//            System.out.println("Extracted Token: " + token);
//            System.out.println("Extracted Email: " + email);
//        }
//
//        // Validate email and set SecurityContext if not already set
//        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//            System.out.println("Loaded UserDetails for Email: " + email);
//
//            if (jwtService.validateToken(token, userDetails)) {
//                System.out.println("Token Validation Successful");
//
//                // Extract role from token
//                String role = jwtService.extractRole(token);
//                System.out.println("Extracted Role: " + role);
//
//                // Create authorities based on the role
//                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
//                System.out.println("Granted Authorities: " + authorities);
//
//                // Create authentication token
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                System.out.println("Authentication Token Created: " + authToken);
//
//                // Set authentication in SecurityContext
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//                System.out.println("SecurityContext Updated with Authentication Token");
//            } else {
//                System.out.println("Token Validation Failed");
//            }
//        } else if (email != null) {
//            System.out.println("Authentication Already Set in SecurityContext");
//        }
//
//        // Continue the filter chain
//        filterChain.doFilter(request, response);
//    }
//}

//@Component
//public class JwtFilter extends OncePerRequestFilter {
//	
//    @Autowired
//    private JWTService jwtService;
//
//    @Autowired
//    private CustomUserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, 
//                                  HttpServletResponse response, 
//                                  FilterChain filterChain)
//            throws ServletException, IOException {
//    	
//        String authHeader = request.getHeader("Authorization");
//        String token = null;
//        String email = null;
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            email = jwtService.extractEmail(token);
//        }
//        
//        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//            if (jwtService.validateToken(token, userDetails)) {
//                // Extract the role from the token
//                String role = jwtService.extractRole(token);
//
//                // Create authorities based on the role
//                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
//
//                // Create an authentication token with the user's authorities
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
//
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                // Set authentication in the security context
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//
//
////        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
////            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
////            
////            if (jwtService.validateToken(token, userDetails)) {
////                UsernamePasswordAuthenticationToken authToken = 
////                    new UsernamePasswordAuthenticationToken(
////                        userDetails, 
////                        null, 
////                        userDetails.getAuthorities());
////                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////                SecurityContextHolder.getContext().setAuthentication(authToken);
////            }
////        }
//        filterChain.doFilter(request, response);
//    }
//}