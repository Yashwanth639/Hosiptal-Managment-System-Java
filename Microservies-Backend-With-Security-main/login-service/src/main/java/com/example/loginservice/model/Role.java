package com.example.loginservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class Role {

    // Primary Key
    @Id
    @Column(name = "roleId")
    private String roleId;

    // Local Attributes
    @Column(name = "roleName", nullable = false, unique = true)
    private String roleName;

    @Column(name = "createdAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.roleId = UUID.randomUUID().toString(); // Generate unique String ID
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Foreign Key Relation Definitions
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @JsonManagedReference // Prevents recursion during JSON serialization
    private List<User> user;

    // Override toString to avoid infinite recursion
    @Override
    public String toString() {
        return "Role{" +
                "roleId='" + roleId + '\'' +
                ", roleName='" + roleName + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}'; // Excludes the user list
    }
}
