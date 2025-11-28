package com.example.loginservice.dto;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import com.example.loginservice.validation.ValidRole;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class RoleDto {

	private String roleId;

	@NotBlank(message = "Role Name cannot be blank")
	@ValidRole
	private String roleName;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
