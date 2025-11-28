package com.example.loginservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.loginservice.config.FeignClientConfiguration;
import com.example.loginservice.dto.MedicalHistoryDto;
import com.example.loginservice.model.ResultResponse;

import jakarta.validation.Valid;

@FeignClient(name = "MEDICAL-HISTORY-SERVICE",configuration = FeignClientConfiguration.class)
public interface MedicalHistoryClient {

	// getPatientMedicalDetailsByPatientId
	@GetMapping("/medical-history/patient/{patientId}")
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetailsByPatientId(
			@Valid @PathVariable String patientId);

}
