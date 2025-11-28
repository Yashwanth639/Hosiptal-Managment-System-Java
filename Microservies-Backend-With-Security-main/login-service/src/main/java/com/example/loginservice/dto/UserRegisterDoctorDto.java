package com.example.loginservice.dto;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class UserRegisterDoctorDto {

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// UserDTO
	@NotBlank(message = "Role id is required")
	private String roleId;

	private String userId;

	@NotBlank(message = "Email id is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).+$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
	private String passwordHash;

	// DoctorDTO
	private String doctorId;

	@NotBlank(message = "Specialization Id is required")
	private String specializationId;

	@NotBlank(message = "Doctor name is required")
	@Pattern(regexp = "^[A-Za-z\\s]+$", message = "Doctor name must only contain alphabets and spaces")
	private String doctorName;
	
	@NotBlank(message="Gender is required")
	@Pattern(regexp="^(Female|Male|Others)$", message="Gender must be either Female, Male, or Others")
	private String gender;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits and may start with '+'")
	private String contactDetails;

}
