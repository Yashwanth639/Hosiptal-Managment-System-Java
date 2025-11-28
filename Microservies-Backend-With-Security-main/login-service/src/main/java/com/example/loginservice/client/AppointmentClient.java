package com.example.loginservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.loginservice.config.FeignClientConfiguration;
import com.example.loginservice.dto.AppointmentDetailsDto;
import com.example.loginservice.dto.BookAppointmentDto;
import com.example.loginservice.dto.RescheduleRequestDto;
import com.example.loginservice.model.ResultResponse;

import jakarta.validation.Valid;

@FeignClient(name = "APPOINTMENT-SERVICE", configuration = FeignClientConfiguration.class)
public interface AppointmentClient {

	// bookAppointment
	@PostMapping("/api/appointments/book")
	public ResponseEntity<ResultResponse<BookAppointmentDto>> bookAppointment(
			@Valid @RequestBody BookAppointmentDto requestDto);

	// rescheduleAppointment
	@PostMapping("/api/appointments/reschedule")
	public ResponseEntity<ResultResponse<RescheduleRequestDto>> rescheduleAppointment(
			@Valid @RequestBody RescheduleRequestDto requestDto);

	// cancelAppointment
	@PostMapping("/api/appointments/cancel/{appointmentId}")
	public ResponseEntity<ResultResponse<Void>> cancelAppointment(@Valid @PathVariable String appointmentId);

	// getCurrentPatientAppointments
	@GetMapping("/api/appointments/current/patient/{patientId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getCurrentPatientAppointments(
			@Valid @PathVariable String patientId);

	// getPastPatientAppointments
	@GetMapping("/api/appointments/past/patient/{patientId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getPastPatientAppointments(
			@Valid @PathVariable String patientId);

	// findPatientAppointmentsByPatientId
	@GetMapping("/api/appointments/patient/{patientId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> findByPatientId(
			@Valid @PathVariable String patientId);

	// filterAppointmentsByDateRange
	@GetMapping("/api/appointments/filter/{startDate}/{endDate}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> filterAppointmentsByDateRange(
			@Valid @PathVariable String startDate, @Valid @PathVariable String endDate);

}
