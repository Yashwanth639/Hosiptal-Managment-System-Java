package com.example.loginservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.loginservice.model.Role;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for handling JWT (JSON Web Token) operations.
 * Provides methods for generating, extracting, and validating JWT tokens.

 */
@Service
public class JWTService {

    private final String secretKey = "qwertyuioppoiuytrewqasdfghjkllkjhgfdsazxcvbnmmnbvcxz";
    
    private static final long EXPIRATION_TIME = 30 * 60 * 1000;

    /**
     * Generates a JWT token for the given user ID, role, and email.
     *
     * @param userId The user ID.
     * @param role   The user role.
     * @param email  The user email.
     * @return The generated JWT token.
     */
    public String getToken(String userId, String email,String role) {
        Map<String, String> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role); // Adding role to the token
        return Jwts.builder().subject(email).claims().add(claims).issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .and().signWith(getKey()).compact();
    }

    /**
     * Retrieves the signing key for JWT tokens.
     *
     * @return The SecretKey for signing tokens.
     */
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the email from the JWT token.
     *
     * @param token The JWT token.
     * @return The extracted email.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the user ID from the JWT token.
     *
     * @param token The JWT token.
     * @return The extracted user ID.
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    /**
     * Extracts the role from the JWT token.
     *
     * @param token The JWT token.
     * @return The extracted role.
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extracts a claim from the JWT token using the provided claim resolver.
     *
     * @param token         The JWT token.
     * @param claimResolver The function to resolve the claim.
     * @param <T>           The type of the claim.
     * @return The extracted claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token.
     * @return The Claims object containing all claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validates the JWT token against the user details.
     *
     * @param token       The JWT token.
     * @param userDetails The user details.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractEmail(token);
        System.out.println(userDetails.getUsername() + "  " + userName);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token The JWT token.
     * @return true if the token is expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}