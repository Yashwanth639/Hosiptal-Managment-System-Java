package com.example.loginservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "user") // Specify the table name
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "userId") // Specify the column name
    private String userId;

    @Column(name = "email", nullable = false, unique = true) // Column for email
    private String email;

    @Column(name = "passwordHash", nullable = false) // Column for encrypted password
    private String passwordHash;

    @Column(name = "createdAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.userId = UUID.randomUUID().toString(); // Generate unique String ID for userId
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Foreign Key Relations
    @ManyToOne
    @JoinColumn(name = "roleId", nullable = false)
    @JsonBackReference // Prevents recursion during JSON serialization
    private Role role;

    // Override toString to avoid infinite recursion
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}'; // Excludes the role reference
    }
}
