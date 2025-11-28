package com.example.appointmentservice.dto;

import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;

import com.example.appointmentservice.enums.AppointmentStatus;
import com.example.appointmentservice.enums.Session;

import jakarta.validation.constraints.Future;
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
public class BookAppointmentDto {

	@NotNull(message = "Appointment date is required")
	@Future(message = "Appointment date must be in the future") // Ensure future date for booking
	private LocalDate appointmentDate;

	@NotNull(message = "Session is required")
	// @Pattern(regexp = "FN|AN", message = "Session must be either 'FN' or 'AN'")
	private Session session;

	@NotBlank(message = "Patient ID is required")
	private String patientId;

	@NotBlank(message = "Doctor ID is required")
	private String doctorId;

	private String availabilityId;

	// @NotBlank(message = "Specialization name is required")
	private String specializationName;

	private String appointmentId;

	private AppointmentStatus status;

	private String patientName;

	private String oldSession;

}