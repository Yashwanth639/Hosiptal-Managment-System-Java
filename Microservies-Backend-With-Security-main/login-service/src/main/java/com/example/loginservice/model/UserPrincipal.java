package com.example.loginservice.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of UserDetails for Spring Security authentication.
 * Wraps a User entity to provide user details for authentication and authorization.
 *
 * @author Vignesh
 * @date 2025-02-03
 */
public class UserPrincipal implements UserDetails {

    private final User user;

    /**
     * Constructs a UserPrincipal with the provided User entity.
     *
     * @param user The User entity to wrap.
     */
    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * Returns the authorities granted to the user.
     *
     * @return A collection of GrantedAuthority objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString()));
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return The user's username.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the account is non-expired, false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Assuming accounts do not expire
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true if the user is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Assuming accounts are not locked
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * @return true if the credentials are non-expired, false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Assuming credentials do not expire
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return true; // Assuming users are enabled
    }
}
