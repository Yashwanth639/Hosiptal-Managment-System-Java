package com.example.notificationservice.dto;

import org.springframework.validation.annotation.Validated;

import com.example.notificationservice.enums.AppointmentStatus;
import com.example.notificationservice.enums.Session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Validated
public class SendReminderDto {

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

	@NotNull(message = "Session is required")
	private Session session;

	@NotNull(message = "Appointment Status is required")
	private AppointmentStatus status;

}
