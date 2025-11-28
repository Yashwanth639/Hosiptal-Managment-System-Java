package com.example.loginservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.loginservice.config.FeignClientConfiguration;
import com.example.loginservice.dto.DoctorAvailabilityDto;

import com.example.loginservice.dto.UserRegisterDoctorDto;

import com.example.loginservice.model.ResultResponse;

import jakarta.validation.Valid;

@FeignClient(name = "DOCTOR-SERVICE",configuration = FeignClientConfiguration.class)
public interface DoctorClient {

	// addDoctorForDoctorRegistration
	@PostMapping("/api/doctors/addDoctor")
	public ResponseEntity<ResultResponse<UserRegisterDoctorDto>> addDoctor(
			@Valid @RequestBody UserRegisterDoctorDto uRDto);

	// getAvailableDoctorsForBookAndRescheduleAppointment
	@GetMapping("/api/doctorAvailability/availableDoctors/{specializationName}/{availableDate}/{session}")
	public ResponseEntity<List<DoctorAvailabilityDto>> getAvailableDoctors(
			@Valid @PathVariable String specializationName, @Valid @PathVariable String availableDate,
			@Valid @PathVariable String session);

}
