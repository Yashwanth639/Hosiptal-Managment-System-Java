package com.cts.medicalhistoryservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class PatientDto {

	private String patientId;

	@NotBlank(message = "Patient name is required")
	private String name;

	@NotNull(message = "Date of birth is required")
	@Past(message = "Date of birth should be a past date")
	private LocalDate dateOfBirth;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits and may start with '+'")
	private String contactDetails;
	
	@NotBlank(message = "Gender is required")
	@Pattern(regexp="^(Female|Male|Others)$", message="Gender must be either Female, Male, or Others")
	private String gender;
	
//	@NotNull(message = "Height in cms is required")
	private Integer heightInCm;
	
//	@NotNull(message = "Weight in kgs is required")
	private Integer weightInKg;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
