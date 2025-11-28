package com.example.doctorservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.doctorservice.security.FeignClientConfiguration;
import com.example.doctorservice.dto.AppointmentDetailsDto;
import com.example.doctorservice.model.ResultResponse;

import jakarta.validation.Valid;

@FeignClient(name = "APPOINTMENT-SERVICE", configuration = FeignClientConfiguration.class) // Name of the Appointment
																							// Service
public interface AppointmentClient {

	// getCurrentDoctorAppointments
	@GetMapping("/api/appointments/current/doctor/{doctorId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getCurrentDoctorAppointments(
			@Valid @PathVariable String doctorId);

	// getPastDoctorAppointments
	@GetMapping("/api/appointments/past/doctor/{doctorId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getPastDoctorAppointments(
			@Valid @PathVariable String doctorId);

	// cancelAppointment
	@PostMapping("/api/appointments/cancel/{appointmentId}")
	public ResponseEntity<ResultResponse<Void>> cancelAppointment(@Valid @PathVariable String appointmentId);

	// completeAppointment
	@PostMapping("/api/appointments/completeAppointment/{appointmentId}")
	public ResponseEntity<ResultResponse<Void>> completeAppointment(@Valid @PathVariable String appointmentId);

	// findDoctorById
	@GetMapping("/api/appointments/doctor/{doctorId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> findByDoctorId(
			@Valid @PathVariable String doctorId);

	// findAppointmentById
	@GetMapping("/api/appointments/{appointmentId}")
	public ResponseEntity<ResultResponse<AppointmentDetailsDto>> findById(@Valid @PathVariable String appointmentId);

	// filterAppointmentsUsingDateRange
	@GetMapping("/api/appointments/filter/{startDate}/{endDate}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> filterAppointmentsByDateRange(
			@Valid @PathVariable String startDate, @Valid @PathVariable String endDate);

}
