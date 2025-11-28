package com.cts.medicalhistoryservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.medicalhistoryservice.dto.DoctorDto;
import com.cts.medicalhistoryservice.dto.SpecializationDto;
import com.cts.medicalhistoryservice.entity.ResultResponse;
import com.cts.medicalhistoryservice.security.FeignClientConfiguration;


import jakarta.validation.Valid;

@FeignClient(name = "DOCTOR-SERVICE", configuration = FeignClientConfiguration.class)
public interface DoctorClient {

	// fetchDoctorById
	@GetMapping("/api/doctors/{doctorId}")
	public ResponseEntity<ResultResponse<DoctorDto>> fetchDoctorById(@Valid @PathVariable String doctorId);

	// findSpecializationById
	@GetMapping("/specialization/id/{specId}")
	public ResponseEntity<ResultResponse<SpecializationDto>> findById(@Valid @PathVariable String specId);

}
