package com.example.doctorservice.dto;

import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;

import com.example.doctorservice.enums.Session;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class AppointmentDetailsDto {

	@NotBlank(message = "Appointment ID is required")
	private String appointmentId;

	@NotNull(message = "Appointment date is required")
	@Future(message = "Appointment date must be in the future") // Ensure future date for booking
	private LocalDate appointmentDate;

	@NotNull(message = "Session is required")
	// @Pattern(regexp = "FN|AN", message = "Session must be either 'FN' or 'AN'")
	private Session session;

	@NotBlank(message = "Status is required")
	@Pattern(regexp = "^(SCHEDULED|COMPLETED|CANCELLED)$", message = "Status must be SCHEDULED, COMPLETED, or CANCELLED")
	private String status;

	@NotBlank(message = "Patient ID is required")
	private String patientId;

	@NotBlank(message = "Doctor id is required")
	private String doctorId;

	@NotBlank(message = "Specialization name is required")
	private String specializationId;

	private String patientName;
	private String doctorName;
	private String specializationName;

}