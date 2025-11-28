package com.example.loginservice.dto;

import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;

import com.example.loginservice.enums.AppointmentStatus;
import com.example.loginservice.enums.Session;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class RescheduleRequestDto {

	@NotNull(message = "New Appointment date is required")
	@Future(message = "Appointment date must be in the future") // Ensure future date for booking
	private LocalDate newAppointmentDate;

	@NotBlank(message = "Appointment Id is required")
	private String appointmentId;

	private String patientId;

	@NotBlank(message = "Doctor Id is required")
	private String doctorId;

	@NotNull(message = "New Session is required")
	// @Pattern(regexp = "FN|AN", message = "Session must be either 'FN' or 'AN'")
	private Session newSession;

	private String oldAvailabilityId;
	
	private LocalDate oldAppointmentDate;
	
	private String oldSession;

	private String newAvailabilityId;

	private AppointmentStatus status;

	private String doctorName;
}