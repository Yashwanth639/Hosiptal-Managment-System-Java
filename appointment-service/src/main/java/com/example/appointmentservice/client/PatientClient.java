package com.example.appointmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.appointmentservice.dto.PatientDto;
import com.example.appointmentservice.model.ResultResponse;
import com.example.appointmentservice.security.FeignClientConfiguration;

import jakarta.validation.Valid;

@FeignClient(name = "LOGIN-SERVICE",configuration = FeignClientConfiguration.class)
public interface PatientClient {

	// fetchPatientById
	@GetMapping("/api/patients/{patientId}")
	public ResponseEntity<ResultResponse<PatientDto>> fetchPatientById(@Valid @PathVariable String patientId);

}
