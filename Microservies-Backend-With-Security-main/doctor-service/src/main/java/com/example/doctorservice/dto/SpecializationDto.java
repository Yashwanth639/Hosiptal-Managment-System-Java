package com.example.doctorservice.dto;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import com.example.doctorservice.validation.ValidSpecialization;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class SpecializationDto {

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private String specializationId;

	@NotBlank(message = "Specialization Name is required")
	@ValidSpecialization
	private String specializationName;
}
