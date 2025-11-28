package com.cts.medicalhistoryservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.medicalhistoryservice.dto.PatientDto;
import com.cts.medicalhistoryservice.entity.ResultResponse;
import com.cts.medicalhistoryservice.security.FeignClientConfiguration;

import jakarta.validation.Valid;

@FeignClient(name = "LOGIN-SERVICE",configuration = FeignClientConfiguration.class)
public interface PatientClient {

	// fetchPatientById
	@GetMapping("/api/patients/{patientId}")
	public ResponseEntity<ResultResponse<PatientDto>> fetchPatientById(@Valid @PathVariable String patientId);

}
