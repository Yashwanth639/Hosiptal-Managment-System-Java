package com.example.notificationservice.security;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * The JWTService class provides methods for generating, validating, and extracting information from JWT tokens.
 */
@Service
public class JWTService {
	
	/**
     * Generates a SecretKey from the base64-encoded secret key string.
     *
     * @return The generated SecretKey.
     */
	private static final String secretkey = "qwertyuioppoiuytrewqasdfghjkllkjhgfdsazxcvbnmmnbvcxz";
  // Token expiration time (e.g., 10 minutes)
	    private static final long EXPIRATION_TIME = 30 * 60 * 1000;
	 

	    private static SecretKey getKey() {
	        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }
	    
	    /**
	     * Extracts all claims from the given JWT token.
	     *
	     * @param token The JWT token.
	     * @return The Claims object containing all claims.
	     */
	    public static Claims extractAllClaims(String token) {
	 
	        return Jwts.parserBuilder()
	                .setSigningKey(getKey())
	                .build()
	                .parseClaimsJws(token)
	                .getBody();
	    }
	   
	    /**
	     * Extracts the roles from the given JWT token.
	     *
	     * @param token The JWT token.
	     * @return The roles as a String.
	     */
	    public String extractRoles(String token) {
	    	return extractAllClaims(token).get("role", String.class);
	    }
	    
	    /**
	     * Extracts the username from the given JWT token.
	     *
	     * @param token The JWT token.
	     * @return The username as a String.
	     */
	    public String extractUsername(String token) {
	        return Jwts.parserBuilder()
	                .setSigningKey(getKey())
	                .build()
	                .parseClaimsJws(token)
	                .getBody()
	                .getSubject(); // Return username
	    }
	    
	    /**
	     * Extracts a specific claim from the given JWT token using the provided claim resolver function.
	     *
	     * @param <T>           The type of the claim.
	     * @param token         The JWT token.
	     * @param claimResolver The function to resolve the claim.
	     * @return The extracted claim.
	     */
	    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
			final Claims claims = extractAllClaims(token);
			return claimResolver.apply(claims);
		}
	    
	    /**
	     * Extracts the email from the given JWT token.
	     *
	     * @param token The JWT token.
	     * @return The email as a String.
	     */
	    public String extractEmail(String token) {
			return extractClaim(token, Claims::getSubject);
		}
	 
	    /**
	     * Extracts the expiration date from the given JWT token.
	     *
	     * @param token The JWT token.
	     * @return The expiration date.
	     */
	    public static Date extractExpiration(String token) {
	        return extractAllClaims(token).getExpiration();
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
	     * Validates the given JWT token.
	     *
	     * @param token The JWT token.
	     * @return True if the token is valid, false otherwise.
	     */
	    public boolean validateToken(String token) {
	        try {
	            Jwts.parserBuilder()
	                .setSigningKey(getKey())
	                .build()
	                .parseClaimsJws(token);
	            return true; // Token is valid
	        } catch (ExpiredJwtException e) {
	            return false; // Token is expired
	        } catch (JwtException e) {
	            return false; // Token is invalid
	        }
	    }
}

