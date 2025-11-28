package com.example.doctorservice.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doctorservice.dto.SpecializationDto;
import com.example.doctorservice.model.ResultResponse;
import com.example.doctorservice.service.SpecializationService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/specialization")
@CrossOrigin(origins = "http://localhost:3000")
public class SpecializationController {

	@Autowired
	private SpecializationService specService;

	// getAllSpecialization
	@GetMapping("/getAll")
	public ResponseEntity<ResultResponse<List<SpecializationDto>>> findAll() {
		List<SpecializationDto> specializations = specService.findAll();
		ResultResponse<List<SpecializationDto>> resultResponse = ResultResponse.<List<SpecializationDto>>builder()
				.success(true).message("Specializations retrieved successfully").data(specializations)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.ok(resultResponse);
	}

	// findSpecializationById
	@GetMapping("/id/{specId}")
	public ResponseEntity<ResultResponse<SpecializationDto>> findById(@Valid @PathVariable String specId) {
		SpecializationDto specialization = specService.findById(specId);
		ResultResponse<SpecializationDto> resultResponse = ResultResponse.<SpecializationDto>builder().success(true)
				.message("Specialization retrieved successfully").data(specialization).timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.ok(resultResponse);
	}

	// findSpecializationByName
	@GetMapping("/name/{specName}")
	public ResponseEntity<ResultResponse<SpecializationDto>> findByName(@Valid @PathVariable String specName) {
		SpecializationDto specialization = specService.findByName(specName);
		ResultResponse<SpecializationDto> resultResponse = ResultResponse.<SpecializationDto>builder().success(true)
				.message("Specialization retrieved successfully").data(specialization).timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.ok(resultResponse);
	}

	// addSpecialization
	@PostMapping("/add")
	public ResponseEntity<ResultResponse<SpecializationDto>> addSpecialization(
			@Valid @RequestBody SpecializationDto sDto) {
		SpecializationDto savedSpecialization = specService.saveSpecialization(sDto);
		ResultResponse<SpecializationDto> resultResponse = ResultResponse.<SpecializationDto>builder().success(true)
				.message("Specialization added successfully").data(savedSpecialization).timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.status(HttpStatus.CREATED).body(resultResponse);
	}

	// updateSpecialization
	@PutMapping("/update/{specId}")
	public ResponseEntity<ResultResponse<SpecializationDto>> updateSpecialization(@Valid @PathVariable String specId,
			@Valid @RequestBody SpecializationDto sDto) {
		SpecializationDto updatedSpecialization = specService.updateSpecialization(specId, sDto);
		ResultResponse<SpecializationDto> resultResponse = ResultResponse.<SpecializationDto>builder().success(true)
				.message("Specialization updated successfully").data(updatedSpecialization)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.ok(resultResponse);
	}

	// deleteSpecializationById
	@DeleteMapping("/delete/{specId}")
	public ResponseEntity<ResultResponse<Void>> deleteById(@Valid @PathVariable String specId) {
		specService.deleteById(specId);
		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("Specialization deleted successfully").data(null).timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resultResponse);
	}

	// deleteAllSpecialization
	@DeleteMapping("/deleteAll")
	public ResponseEntity<ResultResponse<Void>> deleteAllSpecialization() {
		specService.deleteAllSpecialization();
		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("All specializations deleted successfully").data(null).timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resultResponse);
	}

}
