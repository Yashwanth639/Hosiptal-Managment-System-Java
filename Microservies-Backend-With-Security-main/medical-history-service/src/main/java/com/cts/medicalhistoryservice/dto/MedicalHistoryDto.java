package com.cts.medicalhistoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class MedicalHistoryDto {

	private String historyId;

	private String patientId;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private String doctorId;
	
	@NotBlank(message="Blood Pressure cannot be blank")
	private String bloodPressure;
	
	@NotBlank(message="Heart Rate cannot be blank")
	private String heartRate;
	
	@NotBlank(message="Temperature cannot be blank")
	private String temperature;

	@NotBlank(message = "Appointment ID cannot be blank")
	private String appointmentId;

	@NotBlank(message = "Medications cannot be blank")
	private String medications;

	@NotBlank(message = "Treatment cannot be blank")
	private String treatment;

	@NotBlank(message = "Diagnosis cannot be blank")
	private String diagnosis;

	@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date of Visit must be in yyyy-MM-dd format")
	private String dateOfVisit;
	
	private String doctorName;

	private String patientName;
	
	private String specializationName;
}
