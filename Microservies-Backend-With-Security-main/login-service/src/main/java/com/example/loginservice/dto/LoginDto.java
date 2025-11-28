package com.example.loginservice.dto;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import com.example.loginservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class LoginDto {

	@NotBlank(message = "Email Id is required")
	private String email;

	@NotBlank(message = "Password is required")
	private String passwordHash;
	
	private Role role;
	
}
