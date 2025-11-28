package com.example.appointmentservice.dto;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
@Builder
public class DoctorDto {

	private String doctorId; // Unique ID of the doctor (auto-generated)

	@NotBlank(message = "Specialization ID cannot be blank.")
	private String specializationId; // Specialization the doctor belongs to

	@NotBlank(message = "Doctor Name is required.")
	private String name; // Doctor's name
	
	@NotBlank(message="Gender is required")
	@Pattern(regexp="^(Female|Male|Others)$", message="Gender must be either Female, Male, or Others")
	private String gender;

	@NotBlank(message = "Contact details are required.")
	@Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits and may start with '+'")
	private String contactDetails; // Doctor's contact information
}