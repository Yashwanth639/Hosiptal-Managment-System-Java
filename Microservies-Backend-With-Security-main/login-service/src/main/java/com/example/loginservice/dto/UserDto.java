
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
public class UserDto {

	private String userId;

	@NotBlank(message = "Email id is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Role id is required")
	private String roleId;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).+$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
	private String passwordHash;
	
	private String token;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
