package com.example.appointmentservice.dto;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
@Builder
public class SpecializationDto {

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private String specializationId;

	@NotBlank(message = "Specialization Name is required")
	private String specializationName;
}
