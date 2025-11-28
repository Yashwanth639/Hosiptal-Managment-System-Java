package com.example.appointmentservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class DoctorAvailabilityDto {

	private String availabilityId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@NotBlank(message = "Doctor ID cannot be blank.")
	private String doctorId;

	@NotBlank(message = "Specialization ID cannot be blank.")
	private String specializationId;

	@NotNull(message = "Available date is required.")
	@FutureOrPresent(message = "Available date must be today or in the future.")
	private LocalDate availableDate;

	@NotBlank(message = "Session is required.")
	private String session;

	@NotNull(message = "Availability status must be provided. Allowed values are 0,1")
	private Integer isAvailable;
}