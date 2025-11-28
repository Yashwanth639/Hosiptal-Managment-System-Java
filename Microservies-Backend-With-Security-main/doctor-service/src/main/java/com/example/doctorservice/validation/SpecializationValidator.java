package com.example.doctorservice.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.doctorservice.service.SpecializationService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class SpecializationValidator implements ConstraintValidator<ValidSpecialization, String> {

	@Autowired
	private SpecializationService specializationService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty()) {
			return false; // Role cannot be null or empty
		}

		// Normalize the input role (trim and convert to lowercase)
		String normalizedSpecialization = value.trim().toLowerCase();

		// Fetch existing roles from the database and normalize them
		List<String> normalizedExistingSpecializations = specializationService.findAllSpecializationNames().stream()
				.map(role -> role.trim().toLowerCase()).toList();

		// Check if the normalized role already exists
		return !normalizedExistingSpecializations.contains(normalizedSpecialization);
	}
}
