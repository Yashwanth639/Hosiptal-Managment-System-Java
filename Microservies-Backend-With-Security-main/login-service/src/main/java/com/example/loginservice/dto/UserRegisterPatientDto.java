package com.example.loginservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class UserRegisterPatientDto {
	// User details
	private String userId;

	@NotBlank(message = "Email id is required")
	@Email(message = "Invalid email format")
	private String email;

	// @NotNull(message="Role id is required")
	@NotBlank(message = "Role id should not be blank")
	private String roleId;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).+$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
	private String passwordHash;

	// Patient details
	private String patientId;

	@NotBlank(message = "Patient name is required")
	@Pattern(regexp = "^[A-Za-z\\s]+$", message = "Patient name must only contain alphabets and spaces")
	private String name;

	@NotNull(message = "Date of birth is required")
	@Past(message = "Date of birth should be a past date")
	private LocalDate dateOfBirth;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits and may start with '+'")
	private String contactDetails;
	
	@NotBlank(message = "Gender is required")
	@Pattern(regexp="^(Female|Male|Others)$", message="Gender must be either Female, Male, or Others")
	private String gender;
	
//	@NotNull(message = "Height in cms is required")
	private Integer heightInCm;
	
//	@NotNull(message = "Weight in kgs is required")
	private Integer weightInKg;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
