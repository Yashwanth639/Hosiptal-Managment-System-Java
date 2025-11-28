package com.example.doctorservice.dto;

import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;

import com.example.doctorservice.enums.Session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Validated
public class BookAndCancelNotificationDto {

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

	@NotNull(message = "Appointment Date is required")
	private LocalDate appointmentDate;

	@NotNull(message = "Session is required")
	// @Pattern(regexp = "FN|AN", message = "Session must be either 'FN' or 'AN'")
	private Session session;

}
