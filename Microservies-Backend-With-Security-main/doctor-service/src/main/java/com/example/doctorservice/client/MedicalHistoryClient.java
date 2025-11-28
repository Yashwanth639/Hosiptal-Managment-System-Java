package com.example.doctorservice.client;

import com.example.doctorservice.dto.MedicalHistoryDto;
import com.example.doctorservice.model.ResultResponse;
import com.example.doctorservice.security.FeignClientConfiguration;

import jakarta.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="MEDICAL-HISTORY-SERVICE",configuration = FeignClientConfiguration.class)
public interface MedicalHistoryClient {

	// getPatientMedicalDetailsByPatientId
	@GetMapping("/medical-history/patient/{patientId}")
	ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetailsByPatientId(
			@Valid @PathVariable String patientId);

	// getDoctorMedicalDetailsByDoctorId
	@GetMapping("/medical-history/doctor/{doctorId}")
	ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetailsByDoctorId(
			@Valid @PathVariable String doctorId);

//	// getMedicalDetailsByPatientIdStartDateAndEndDate
//	@GetMapping("/medical-history/patient/{patientId}/date")
//	ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetailsByPatientInfoAndDateVisit(
//			@Valid @PathVariable String patientId, @Valid @RequestParam String startDate,
//			@Valid @RequestParam String endDate);

	// addMedicalRecord
	@PostMapping("/medical-history/add")
	ResponseEntity<ResultResponse<MedicalHistoryDto>> addMedicalRecord(
			@Valid @RequestBody MedicalHistoryDto medicalHistoryDto);

	// getMedicalDetailsByDoctorIdAndPatientId
	@GetMapping("/medical-history/between/{doctorId}/{patientId}")
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetialsByDoctorIdAndPatientId(
			@Valid @PathVariable String doctorId, @Valid @PathVariable String patientId);
	
	// getMedicalDetailsByPatientIdAndDateRange
		@GetMapping("/medical-history/filterByDate/{startDate}/{endDate}/{patientId}")
		public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> filterMedicalDetailsByPatientIdAndDateRange(@Valid @PathVariable String startDate, @Valid @PathVariable String endDate, @Valid @PathVariable String patientId);

}
