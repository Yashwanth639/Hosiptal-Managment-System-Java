package com.cts.medicalhistoryservice.controller;

import com.cts.medicalhistoryservice.dto.MedicalHistoryDto;
import com.cts.medicalhistoryservice.entity.ResultResponse;
import com.cts.medicalhistoryservice.exception.UnauthorizedAccessException;
import com.cts.medicalhistoryservice.service.MedicalHistoryService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/medical-history")
@CrossOrigin(origins = "http://localhost:3000")
public class MedicalHistoryController {

	@Autowired
	private MedicalHistoryService medicalHistoryService;

	// getAllMedicalHistories
	@GetMapping("/getAll")
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> findAll() {
		List<MedicalHistoryDto> medicalRecords = medicalHistoryService.findAll();
		ResultResponse<List<MedicalHistoryDto>> response = ResultResponse.<List<MedicalHistoryDto>>builder()
				.data(medicalRecords).success(true).message("Retrieved all the medical records")
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// getMedicalHistoryById
	@GetMapping("/{medicalHistoryId}")
	public ResponseEntity<ResultResponse<MedicalHistoryDto>> findById(@Valid @PathVariable String medicalHistoryId) {
		MedicalHistoryDto medicalHistory = medicalHistoryService.findById(medicalHistoryId);
		ResultResponse<MedicalHistoryDto> response = ResultResponse.<MedicalHistoryDto>builder().data(medicalHistory)
				.success(true).message("Retrieved medical history with id : " + medicalHistoryId)
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// updateMedicalHistoryById
	@PutMapping("/update/{mhId}")
	public ResponseEntity<ResultResponse<MedicalHistoryDto>> updateMedicalHistory(@Valid @PathVariable String mhId,
			@Valid @RequestBody MedicalHistoryDto mhDto) {
		MedicalHistoryDto medicalHistory = medicalHistoryService.updateMedicalHistory(mhId, mhDto);
		ResultResponse<MedicalHistoryDto> response = ResultResponse.<MedicalHistoryDto>builder().data(medicalHistory)
				.success(true).message("Updated medical history with id : " + mhId).timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// deleteMedicalHistoryById
	@DeleteMapping("/delete/{mhId}")
	public ResponseEntity<ResultResponse<Void>> deleteById(@Valid @PathVariable String mhId) {
		medicalHistoryService.deleteById(mhId);
		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("Medical History deleted successfully with id : " + mhId).data(null)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resultResponse);
	}

	// deleteAllMedicalHistories
	@DeleteMapping("/deleteAll")
	public ResponseEntity<ResultResponse<Void>> deleteMedicalHistory() {
		medicalHistoryService.deleteAllMedicalHistory();
		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("All medical history records deleted successfully").data(null).timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resultResponse);
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method returns the medical records of a particular patient using the
	 * patient ID.
	 *
	 * @param patientId The ID of the patient whose medical records are to be
	 *                  retrieved.
	 * @return ResponseEntity containing the result response with the list of
	 *         medical history DTOs.
	 */
	// getPatientMedicalDetailsByPatientId
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetailsByPatientId(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String patientId) {

//		// Ensure the logged-in user can only update their own patient details
//		if (!userId.equals(patientId)) {
//			log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
//			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
//		}

		log.info("MedicalHistoryController::Getting medical records by patient ID:Entry");

		List<MedicalHistoryDto> medicalHistories = medicalHistoryService.getMedicalDetailsByPatientId(patientId);
		ResultResponse<List<MedicalHistoryDto>> resultResponse = ResultResponse.<List<MedicalHistoryDto>>builder()
				.success(true).message("Medical records retrieved successfully").data(medicalHistories)
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("MedicalHistoryController::Getting medical records by patient ID:Exit");

		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	/**
	 * This method returns the medical records of patients for a particular doctor
	 * using the doctor ID.
	 *
	 * @param doctorId The ID of the doctor whose patients' medical records are to
	 *                 be retrieved.
	 * @return ResponseEntity containing the result response with the list of
	 *         medical history DTOs.
	 */
	// getDoctorMedicalDetailsByDoctorId
	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetailsByDoctorId(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String doctorId) {

		// Ensure the logged-in user can only update their own doctor details
		if (!userId.equals(doctorId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
		}

		log.info("MedicalHistoryController::Getting medical records by Doctor ID:Entry");

		List<MedicalHistoryDto> medicalHistoryDto = medicalHistoryService.getMedicalDetailsByDoctorId(doctorId);
		ResultResponse<List<MedicalHistoryDto>> resultResponse = ResultResponse.<List<MedicalHistoryDto>>builder()
				.success(true).message("Medical records retrieved successfully").data(medicalHistoryDto)
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("MedicalHistoryController::Getting medical records by Doctor ID:Exit");
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	/**
	 * This method returns the medical records of a particular patient between
	 * specific dates.
	 *
	 * @param patientId The ID of the patient whose medical records are to be
	 *                  retrieved.
	 * @param startDate The start date of the period for which medical records are
	 *                  to be retrieved.
	 * @param endDate   The end date of the period for which medical records are to
	 *                  be retrieved.
	 * @return ResponseEntity containing the result response with the list of
	 *         medical history DTOs.
	 */
//	// getMedicalDetailsAndPatientIdAndDateRange
//	@GetMapping("/patient/{patientId}/date")
//	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetailsByPatientInfoAndDateVisit(
//			@RequestAttribute("userId") String userId,
//			@Valid @PathVariable String patientId, @Valid @RequestParam String startDate,
//			@Valid @RequestParam String endDate) {
//		
//		// Ensure the logged-in user can only update their own patient details
//	    if (!userId.equals(patientId)) {
//	        log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
//	        throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
//	    }
//
//		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
//
//		try {
//			LocalDate parsedStartDate = LocalDate.parse(startDate, formatter);
//			LocalDate parsedEndDate = LocalDate.parse(endDate, formatter);
//
//			log.info(
//					"MedicalhistoryController::Getting medical records by patient ID with StartDate and EndDate: Entry");
//
//			List<MedicalHistoryDto> medicalHistories = medicalHistoryService
//					.getMedicalDetailsByPatientIdAndDateOfVisit(patientId, parsedStartDate, parsedEndDate);
//			ResultResponse<List<MedicalHistoryDto>> resultResponse = ResultResponse.<List<MedicalHistoryDto>>builder()
//					.success(true).message("Medical records retrieved successfully").data(medicalHistories)
//					.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();
//
//			log.info("MedicalhistoryController::Getting medical records by patient ID with StartDate and EndDate:Exit");
//
//			return new ResponseEntity<>(resultResponse, HttpStatus.OK);
//		} catch (DateTimeParseException e) {
//			ResultResponse<List<MedicalHistoryDto>> errorResponse = ResultResponse.<List<MedicalHistoryDto>>builder()
//					.success(false).message("Invalid date format. Please use ISO_LOCAL_DATE (yyyy-MM-dd)").data(null)
//					.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();
//			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//		}
//	}

	/**
	 * This method adds a new medical record to the medical history of a particular
	 * patient using the appointment ID.
	 *
	 * @param medicalHistoryDto The DTO containing the details of the medical record
	 *                          to be added.
	 * @return ResponseEntity containing the result response with the added medical
	 *         history DTO.
	 */
	// addMedicalRecord
	@PostMapping("/add")
	public ResponseEntity<ResultResponse<MedicalHistoryDto>> addMedicalRecord(
			@Valid @RequestBody MedicalHistoryDto medicalHistoryDto) {
		log.info("MedicalHistoryController::addMedicalRecord:Entry");

		MedicalHistoryDto addedRecord = medicalHistoryService.addMedicalRecords(medicalHistoryDto);
		ResultResponse<MedicalHistoryDto> resultResponse = ResultResponse.<MedicalHistoryDto>builder().success(true)
				.message("Medical record added successfully").data(addedRecord)
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("MedicalHistoryController::addMedicalRecord:Exit");
		return new ResponseEntity<>(resultResponse, HttpStatus.CREATED);
	}

	// getMedicalDetailsByDoctorIdAndPatientId
	@GetMapping("/between/{doctorId}/{patientId}")
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetialsByDoctorIdAndPatientId(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String doctorId,
			@Valid @PathVariable String patientId) {

		// Ensure the logged-in user can only update their own doctor details
		if (!userId.equals(doctorId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
		}

		List<MedicalHistoryDto> history = medicalHistoryService.getMedicalDetialsByDoctorIdAndPatientId(doctorId,
				patientId);
		ResultResponse<List<MedicalHistoryDto>> resultResponse = ResultResponse.<List<MedicalHistoryDto>>builder()
				.success(true).message("Medical records between patient and doctor are retrieved successfully")
				.data(history).timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("MedicalHistoryController::addMedicalRecord:Exit");
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// getPatientMedicalDetailsWithinDateRange --> 9/4/2025
	@GetMapping("/filterByDate/{startDate}/{endDate}/{patientId}")
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> filterMedicalDetailsByPatientIdAndDateRange(
			@RequestAttribute("userId") String userId, @Valid @PathVariable LocalDate startDate,
			@Valid @PathVariable LocalDate endDate, @Valid @PathVariable String patientId) {

//		// Ensure the logged-in user can only update their own patient details
//		if (!userId.equals(patientId)) {
//			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, patientId);
//			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
//		}

		List<MedicalHistoryDto> details = medicalHistoryService.getMedicalDetailsWithinDateRange(startDate, endDate,
				patientId);
		ResultResponse<List<MedicalHistoryDto>> response = ResultResponse.<List<MedicalHistoryDto>>builder()
				.success(true).message("Successfully retrieved Patient Medical Records within date range").data(details)
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}