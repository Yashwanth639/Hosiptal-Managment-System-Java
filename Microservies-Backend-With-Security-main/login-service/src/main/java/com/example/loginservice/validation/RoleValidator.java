package com.example.loginservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.loginservice.service.RoleService;

@Component
public class RoleValidator implements ConstraintValidator<ValidRole, String> {

	@Autowired
	private RoleService roleService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty()) {
			return false; // Role cannot be null or empty
		}

		// Normalize the input role (trim and convert to lowercase)
		String normalizedRole = value.trim().toLowerCase();

		// Fetch existing roles from the database and normalize them
		List<String> normalizedExistingRoles = roleService.findAllRoleNames().stream()
				.map(role -> role.trim().toLowerCase()).toList();

		// Check if the normalized role already exists
		return !normalizedExistingRoles.contains(normalizedRole);
	}
}
