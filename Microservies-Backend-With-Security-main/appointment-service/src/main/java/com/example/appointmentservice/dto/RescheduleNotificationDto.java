package com.example.appointmentservice.dto;

import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;

import com.example.appointmentservice.enums.Session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class RescheduleNotificationDto {

	@NotBlank(message = "Appointment ID is required")
	private String appointmentId;

	@NotBlank(message = "Patient ID is required")
	private String patientId;

	@NotBlank(message = "Doctor ID is required")
	private String doctorId;

	@NotBlank(message = "Patient Name is required")
	private String patientName;

	@NotBlank(message = "Doctor Name is required")
	private String doctorName;

	@NotNull(message = "Old date is required")
	private LocalDate oldDate;

	@NotNull(message = "Old session is required")
	// @Pattern(regexp = "FN|AN", message = "Session must be either 'FN' or 'AN'")
	private Session oldSession;

	@NotNull(message = "New Date is required")
	private LocalDate newDate;

	@NotNull(message = "New Session is required")
	// @Pattern(regexp = "FN|AN", message = "Session must be either 'FN' or 'AN'")
	private Session newSession;
}
