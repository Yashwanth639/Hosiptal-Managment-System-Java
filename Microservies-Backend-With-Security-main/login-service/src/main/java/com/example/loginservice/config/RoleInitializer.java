package com.example.loginservice.config;

import com.example.loginservice.model.Role;
import com.example.loginservice.repository.RoleRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        createRoleIfNotFound("ROLE_DOCTOR");
        createRoleIfNotFound("ROLE_PATIENT");
    }

    private void createRoleIfNotFound(String roleName) {
        try {
            // Check if role already exists
            Role role = roleRepository.findByRoleName(roleName).orElse(null);
            if (role == null) {
                // Create and save the new role
                role = new Role();
                role.setRoleName(roleName);
                roleRepository.save(role);
                log.info("Role created: {}", roleName);
            } else {
                log.info("Role already exists: {}", roleName);
            }
        } catch (Exception e) {
            log.error("Error initializing role: {} - {}", roleName, e.getMessage());
        }
    }
}
