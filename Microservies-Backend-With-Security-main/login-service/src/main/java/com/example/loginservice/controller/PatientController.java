package com.example.loginservice.controller;

import com.example.loginservice.dto.AppointmentDetailsDto;
import com.example.loginservice.dto.BookAppointmentDto;
import com.example.loginservice.dto.DoctorAvailabilityDto;
import com.example.loginservice.dto.MedicalHistoryDto;
import com.example.loginservice.dto.NotificationDto;
import com.example.loginservice.dto.PatientDto;
import com.example.loginservice.dto.RescheduleRequestDto;
import com.example.loginservice.exception.UnauthorizedAccessException;
import com.example.loginservice.service.PatientService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.example.loginservice.model.ResultResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:3000")
public class PatientController {

	@Autowired
	private PatientService patientService;

	/**
	 * Endpoint for testing the controller.
	 */
	// testEndPoint
	@GetMapping("/test")
	public ResponseEntity<String> testEndpoint() {
		log.info("Test endpoint called");
		return ResponseEntity.ok("PatientController is working!");
	}

	// getAllPatients
	@GetMapping("/getAll")
	public ResponseEntity<ResultResponse<List<PatientDto>>> findAll() {
		log.info("PatientController::findAll:Entry");

		List<PatientDto> patients = patientService.findAll();

		ResultResponse<List<PatientDto>> resultResponse = ResultResponse.<List<PatientDto>>builder().success(true)
				.message("Patients retrieved successfully").data(patients).timestamp(LocalDateTime.now(ZoneOffset.UTC))
				.build();

		log.info("PatientController::findAll:Exit successfully. Retrieved {} patients.", patients.size());
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// fetchPatientById
	@GetMapping("/{patientId}")
	public ResponseEntity<ResultResponse<PatientDto>> fetchPatientById(@RequestAttribute("userId") String userId,
			@Valid @PathVariable String patientId) {

//		// Ensure the logged-in user can only update their own patient details
//		if (!userId.equals(patientId)) {
//			log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
//			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
//		}

		log.info("PatientController::fetchPatientById:Entry for Patient ID: {}", patientId);

		PatientDto patient = patientService.fetchPatientById(patientId);

		ResultResponse<PatientDto> resultResponse = ResultResponse.<PatientDto>builder().success(true)
				.message("Fetched patient by ID successfully").data(patient)
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("PatientController::fetchPatientById:Exit successfully for Patient ID: {}", patientId);
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// fetchPatientByName
	@GetMapping("/name/{pName}")
	public ResponseEntity<ResultResponse<PatientDto>> findByName(@Valid @PathVariable String pName) {
		log.info("PatientController::findByName:Entry for Patient Name: {}", pName);

		PatientDto patient = patientService.findByName(pName);

		ResultResponse<PatientDto> resultResponse = ResultResponse.<PatientDto>builder().success(true)
				.message("Fetched patient by name successfully").data(patient)
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("PatientController::findByName:Exit successfully for Patient Name: {}", pName);
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// updatePatient
	@PutMapping("/update/{patientId}")
	public ResponseEntity<ResultResponse<PatientDto>> updatePatient(@RequestAttribute("userId") String userId,
			@PathVariable String patientId, @Valid @RequestBody PatientDto pDto) {
		log.info("PatientController::updatePatient:Entry for Patient ID: {}", patientId);

		// Ensure the logged-in user can only update their own patient details
		if (!userId.equals(patientId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
		}

		PatientDto updatedPatient = patientService.updatePatient(patientId, pDto);

		ResultResponse<PatientDto> resultResponse = ResultResponse.<PatientDto>builder().success(true)
				.message("Patient updated successfully").data(updatedPatient)
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("PatientController::updatePatient:Exit successfully for Patient ID: {}", patientId);
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// deletePatientById
	@DeleteMapping("/delete/{patientId}")
	public ResponseEntity<ResultResponse<Void>> deleteById(@RequestAttribute("userId") String userId,
			@Valid @PathVariable String patientId) {
		log.info("PatientController::deleteById:Entry for Patient ID: {}", patientId);

		// Ensure the logged-in user can only update their own patient details
		if (!userId.equals(patientId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
		}

		patientService.deleteById(patientId);

		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("Patient deleted successfully").data(null).timestamp(LocalDateTime.now(ZoneOffset.UTC))
				.build();

		log.info("PatientController::deleteById:Exit successfully for Patient ID: {}", patientId);
		return new ResponseEntity<>(resultResponse, HttpStatus.NO_CONTENT);
	}

	// deleteAllPatients
	@DeleteMapping("/deleteAll")
	public ResponseEntity<ResultResponse<Void>> deleteAllPatients() {
		log.info("PatientController::deleteAllPatients:Entry");

		patientService.deleteAllPatients();

		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("All patients deleted successfully").data(null).timestamp(LocalDateTime.now(ZoneOffset.UTC))
				.build();

		log.info("PatientController::deleteAllPatients:Exit successfully");
		return new ResponseEntity<>(resultResponse, HttpStatus.NO_CONTENT);
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------

	// getCurrentPatientAppointments
	@GetMapping("/current/patient/{patientId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getCurrentPatientAppointments(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String patientId) {

		// Ensure the logged-in user can only update their own patient details
		if (!userId.equals(patientId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
		}

		return patientService.getCurrentPatientAppointments(patientId);
	}

	// getPastPatientAppointments
	@GetMapping("/past/patient/{patientId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getPastPatientAppointments(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String patientId) {

		// Ensure the logged-in user can only update their own patient details
		if (!userId.equals(patientId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
		}

		return patientService.getPastPatientAppointments(patientId);
	}

	// getPatientMedicalDetailsByPatientId
	@GetMapping("/medicalHistory/{patientId}")
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetailsByPatientId(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String patientId) {

		// Ensure the logged-in user can only update their own patient details
		if (!userId.equals(patientId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
		}

		ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> result = patientService
				.getMedicalDetailsByPatientId(patientId);
		List<MedicalHistoryDto> medicalHistory = result.getBody().getData();
		ResultResponse<List<MedicalHistoryDto>> response = ResultResponse.<List<MedicalHistoryDto>>builder()
				.data(medicalHistory).success(true).message("Successfully retrieved the patient's medical histories")
				.timestamp(LocalDateTime.now()).build();
		log.info("Successfully retrieved the patient's medical histories");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// findPatientAppointmentsByPatientId
	@GetMapping("/appointment/{patientId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> findAppointmentByPatientId(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String patientId) {

		// Ensure the logged-in user can only update their own patient details
		if (!userId.equals(patientId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
		}

		return patientService.findAppointmentByPatientId(patientId);
	}

	// getAvailableDoctorsForBookingAndReschedulingAppointment
	@GetMapping("/availableDoctors/{specializationName}/{availableDate}/{session}")
	public ResponseEntity<List<DoctorAvailabilityDto>> getAvailableDoctors(
			@Valid @PathVariable String specializationName, @Valid @PathVariable LocalDate availableDate,
			@Valid @PathVariable String session) {
		return patientService.getAvailableDoctors(specializationName, availableDate, session);
	}

	// ----------------------------------------------------------------------------------------------------------------------------
	// bookAppointment
	@PostMapping("/bookAppointment")
	public ResponseEntity<ResultResponse<BookAppointmentDto>> bookAppointment(
			@RequestBody @Valid BookAppointmentDto rDto) {
		System.out.println("Received Request to Book Appointment");
		System.out.println("SecurityContext Authentication in Controller: "
				+ SecurityContextHolder.getContext().getAuthentication());
		ResponseEntity<ResultResponse<BookAppointmentDto>> appDto = patientService.bookAppointment(rDto);
		BookAppointmentDto appointment = appDto.getBody().getData();
		ResultResponse<BookAppointmentDto> response = ResultResponse.<BookAppointmentDto>builder().data(appointment)
				.success(true).message("Patient successfully booked an appointment").timestamp(LocalDateTime.now())
				.build();
		log.info("Patient successfully booked an appointment");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// rescheduleAppointment
	@PostMapping("/rescheduleAppointment")
	public ResponseEntity<ResultResponse<RescheduleRequestDto>> rescheduleAppointment(
			@RequestBody @Valid RescheduleRequestDto rDto) {
		ResponseEntity<ResultResponse<RescheduleRequestDto>> rescheduleDto = patientService.rescheduleAppointment(rDto);
		RescheduleRequestDto appointment = rescheduleDto.getBody().getData();
		ResultResponse<RescheduleRequestDto> response = ResultResponse.<RescheduleRequestDto>builder().data(appointment)
				.success(true).message("Patient successfully rescheduled an appointment").timestamp(LocalDateTime.now())
				.build();
		log.info("Patient successfully rescheduled an appointment");
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// cancelAppointmentByAppointmentId
	@PostMapping("/cancelAppointment/{appointmentId}")
	public ResponseEntity<ResultResponse<Void>> cancelAppointment(@Valid @PathVariable String appointmentId) {
		patientService.cancelAppointment(appointmentId);
		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().data(null).success(true)
				.message("Cancelled appointment with id : " + appointmentId).timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resultResponse);

	}

	// ------------------------------------------------------------------------------------------------------------------------------

	// getPatientNotificationsByPatientId
	@GetMapping("/notifications/{patientId}")
	public ResponseEntity<ResultResponse<List<NotificationDto>>> getPatientNotificationsByPatientId(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String patientId) {

		// Ensure the logged-in user can only update their own patient details
		if (!userId.equals(patientId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
		}

		return patientService.getPatientNotifications(patientId);
	}

	// markPatientNotificationAsRead
	@PutMapping("/notifications/markAsRead/{notificationId}")
	public ResponseEntity<ResultResponse<Void>> markDoctorNotificationAsRead(
			@Valid @PathVariable String notificationId) {
		return patientService.markPatientNotificationAsRead(notificationId);
	}

	// -------------------------------------------------------------------------------------------------------------------

	// filterPatientAppointmentsByDateRange --> 9/4/2025
	@GetMapping("/filterAppointmentsByDate/{startDate}/{endDate}/{patientId}/{appointmentStatus}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> filterPatientAppointmentsByDateRange(
			@RequestAttribute("userId") String userId,
			@Valid @PathVariable String startDate, @Valid @PathVariable String endDate,
			@Valid @PathVariable String patientId, @Valid @PathVariable String appointmentStatus) {
		
		// Ensure the logged-in user can only update their own patient details
				if (!userId.equals(patientId)) {
					log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
					throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
				}
				
		List<AppointmentDetailsDto> appointments = patientService.filterPatientAppointmentByDateRange(startDate,
				endDate, patientId, appointmentStatus);
		ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
				.success(true).data(appointments)
				.message("Successfully retrieved patient appointments within date range").timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
