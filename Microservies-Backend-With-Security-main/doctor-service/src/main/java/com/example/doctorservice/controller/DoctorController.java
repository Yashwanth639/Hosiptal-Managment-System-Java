package com.example.doctorservice.controller;

import com.example.doctorservice.dto.AppointmentDetailsDto;

import com.example.doctorservice.dto.DoctorDto;
import com.example.doctorservice.dto.MedicalHistoryDto;
import com.example.doctorservice.dto.NotificationDto;
import com.example.doctorservice.dto.UserRegisterDoctorDto;
import com.example.doctorservice.exceptions.UnauthorizedAccessException;
import com.example.doctorservice.model.ResultResponse;
import com.example.doctorservice.service.DoctorAvailabilityService;
import com.example.doctorservice.service.DoctorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {

	@Autowired
	private DoctorService doctorService;

	/**
	 * Get all doctors.
	 *
	 * @return List of DoctorDto
	 */
	// getAllDoctors
	@GetMapping("/getAll")
	public ResponseEntity<ResultResponse<List<DoctorDto>>> getAllDoctors() {
		log.info("Received request to fetch all doctors.");

		// Call service method to fetch all doctors
		List<DoctorDto> doctors = doctorService.findAll();

		log.info("Successfully fetched {} doctor(s).", doctors.size());
		ResultResponse<List<DoctorDto>> response = ResultResponse.<List<DoctorDto>>builder().data(doctors).success(true)
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Fetch doctor record by ID
	@GetMapping("/{doctorId}")
	public ResponseEntity<ResultResponse<DoctorDto>> fetchDoctorById(@RequestAttribute("userId") String userId,
			@Valid @PathVariable String doctorId) {
		log.info("Received request to fetch doctor with ID: {}", doctorId);

//		// Ensure the logged-in user can only update their own doctor details
//		if (!userId.equals(doctorId)) {
//			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
//			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
//		}

		// Call the service layer
		DoctorDto doctor = doctorService.fetchDoctorById(doctorId);

		ResultResponse<DoctorDto> doctorResponse = ResultResponse.<DoctorDto>builder().data(doctor).success(true)
				.message("Fetched doctor by id").timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(doctorResponse, HttpStatus.OK);

	}

	// addDoctor
	@PostMapping("/addDoctor")
	public ResponseEntity<ResultResponse<UserRegisterDoctorDto>> addDoctor(
			@Valid @RequestBody UserRegisterDoctorDto uRDto) {
		UserRegisterDoctorDto doctor = doctorService.addDoctor(uRDto);
		ResultResponse<UserRegisterDoctorDto> response = ResultResponse.<UserRegisterDoctorDto>builder().data(doctor)
				.success(true).message("Doctor Registration successful").timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// findDoctorsBySpecializationName
	@GetMapping("/specialization/name/{specializationName}")
	public ResponseEntity<ResultResponse<List<DoctorDto>>> getDoctorsListBySpecializationName(
			@Valid @PathVariable String specializationName) {
		List<DoctorDto> doctors = doctorService.getDoctorsListBySpecializationName(specializationName);
		ResultResponse<List<DoctorDto>> doctorResponse = ResultResponse.<List<DoctorDto>>builder().data(doctors)
				.success(true).message("Fetched doctor by specialization name").timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(doctorResponse, HttpStatus.OK);
	}

	// updateDoctor
	@PutMapping("/update/{doctorId}")
	public ResponseEntity<ResultResponse<DoctorDto>> updateDoctor(@RequestAttribute("userId") String userId,
			@Valid @PathVariable String doctorId, @Valid @RequestBody DoctorDto dDto) {

//		// Ensure the logged-in user can only update their own doctor details
//		if (!userId.equals(doctorId)) {
//			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
//			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
//		}

		DoctorDto doctor = doctorService.updateDoctor(doctorId, dDto);
		ResultResponse<DoctorDto> doctorResponse = ResultResponse.<DoctorDto>builder().data(doctor).success(true)
				.message("Updated doctor by id").timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(doctorResponse, HttpStatus.OK);
	}

	// deleteDoctorByDoctorId
	@DeleteMapping("/delete/{doctorId}")
	public ResponseEntity<ResultResponse<Void>> deleteById(@RequestAttribute("userId") String userId,
			@Valid @PathVariable String doctorId) {

		// Ensure the logged-in user can only update their own doctor details
		if (!userId.equals(doctorId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
		}

		doctorService.deleteById(doctorId);
		ResultResponse<Void> response = ResultResponse.<Void>builder().data(null)
				.message("Successfully deleted doctor by id: " + doctorId).success(true).timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	}

	// deleteAllDoctors
	@DeleteMapping("/deleteAll")
	public ResponseEntity<ResultResponse<Void>> deleteAllDoctors() {
		doctorService.deleteAllDoctors();
		ResultResponse<Void> response = ResultResponse.<Void>builder().data(null)
				.message("Successfully deleted all doctors").success(true).timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Fetch doctors by specialization ID
	 *
	 * @param specializationId the specialization ID to filter doctors
	 * @return List of DoctorDto objects
	 */
	// fetchDoctorListBySpecializationId
	@GetMapping("/specialization/id/{specializationId}")
	public ResponseEntity<ResultResponse<List<DoctorDto>>> fetchDoctorsBySpecializationId(
			@Valid @PathVariable String specializationId) {
		log.info("Received request to fetch doctors for specializationId: {}", specializationId);

		// Call the service layer to fetch doctors
		List<DoctorDto> doctors = doctorService.fetchDoctorsBySpecializationId(specializationId);

		log.info("Successfully fetched {} doctor(s) for specializationId: {}", doctors.size(), specializationId);

		ResultResponse<List<DoctorDto>> response = ResultResponse.<List<DoctorDto>>builder().data(doctors).success(true)
				.message("Successfully fetched doctors using specialization Id: " + specializationId)
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// -------------------------------------------------------------------------------------------------------------------------------------

	// getCurrentDoctorAppointments
	@GetMapping("/current/appointments/{doctorId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getCurrentDoctorAppointments(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String doctorId) {

//		// Ensure the logged-in user can only update their own doctor details
//		if (!userId.equals(doctorId)) {
//			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
//			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
//		}

		log.info("Received request to fetch current appointments for doctor with ID: {}", doctorId);

		ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> resultResponse = doctorService
				.getCurrentDoctorAppointments(doctorId);

		ResultResponse<List<AppointmentDetailsDto>> result = resultResponse.getBody(); // --> from type
																						// ResponseEntity<ResultResponse<?>>
																						// to ResultResponse<?>

		List<AppointmentDetailsDto> appointmentDetailsList = result.getData(); // --> from type ResulResponse<?> to ?

		ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
				.data(appointmentDetailsList).success(true).timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);

	}

	// getPastDoctorAppointments
	@GetMapping("/past/appointments/{doctorId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getPastDoctorAppointments(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String doctorId) {

		// Ensure the logged-in user can only update their own doctor details
		if (!userId.equals(doctorId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
		}

		log.info("Received request to fetch current appointments for doctor with ID: {}", doctorId);

		ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> resultResponse = doctorService
				.getPastDoctorAppointments(doctorId);

		ResultResponse<List<AppointmentDetailsDto>> result = resultResponse.getBody(); // --> from type
																						// ResponseEntity<ResultResponse<?>>
																						// to ResultResponse<?>

		List<AppointmentDetailsDto> appointmentDetailsList = result.getData(); // --> from type ResulResponse<?> to ?

		ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
				.data(appointmentDetailsList).success(true).timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);

	}

	// -------------------------------------------------------------------------------------------------------------------------------------

	// getMedicalDetailsByPatientId
	@GetMapping("/medical-history/patient/{patientId}")
	ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetailsByPatientId(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String patientId) {

//		// Ensure the logged-in user can only update their own patient details
//		if (!userId.equals(patientId)) {
//			log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
//			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
//		}

		ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> result = doctorService
				.getMedicalHistoryByPatientId(patientId);
		List<MedicalHistoryDto> medicalHistory = result.getBody().getData();
		ResultResponse<List<MedicalHistoryDto>> response = ResultResponse.<List<MedicalHistoryDto>>builder()
				.data(medicalHistory).success(true).message("Successfully retrieved patient's medical history")
				.timestamp(LocalDateTime.now()).build();
		log.info("Successfully retrieved patient's medical history");
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// getMedicalDetailsByDoctorId
	@GetMapping("/medical-history/doctor/{doctorId}")
	ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetailsByDoctorId(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String doctorId) {

//		// Ensure the logged-in user can only update their own doctor details
//		if (!userId.equals(doctorId)) {
//			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
//			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
//		}

		ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> result = doctorService
				.getMedicalHistoryByDoctorId(doctorId);
		List<MedicalHistoryDto> medicalHistory = result.getBody().getData();
		ResultResponse<List<MedicalHistoryDto>> response = ResultResponse.<List<MedicalHistoryDto>>builder()
				.data(medicalHistory).success(true).message("Successfully retrieved doctor's medical history")
				.timestamp(LocalDateTime.now()).build();
		log.info("Successfully retrieved doctor's medical history");
		return new ResponseEntity<>(response, HttpStatus.OK);
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

		return doctorService.getMedicalDetialsByDoctorIdAndPatientId(doctorId, patientId);
	}

	// findAppointmentByDoctorId
	@GetMapping("/appointment/{doctorId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> findAppointmentByDoctorId(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String doctorId) {

		// Ensure the logged-in user can only update their own doctor details
		if (!userId.equals(doctorId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
		}

		return doctorService.findAppointmentByDoctorId(doctorId);
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// markAppointmentAsCompleted
	@PostMapping("/markAsCompleted")
	public ResponseEntity<ResultResponse<MedicalHistoryDto>> markAppointmentAsCompleted(
			@RequestBody @Valid MedicalHistoryDto medicalHistoryDto) {
		return doctorService.markAppointmentAsCompleted(medicalHistoryDto);
	}

	// cancelAppointmentById
	@PostMapping("/cancel/{appointmentId}")
	public ResponseEntity<ResultResponse<Void>> cancelAppointmentById(@Valid @PathVariable String appointmentId) {
		log.info("Received request to cancel appointment with ID: {}", appointmentId);

		doctorService.cancelAppointmentById(appointmentId);
		ResultResponse<Void> response = ResultResponse.<Void>builder().data(null).success(true)
				.message("Successfully cancelled appointment by id : " + appointmentId).timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// ---------------------------------------------------------------------------------------------------------------------

	// getDoctorNotificationsByDoctorId
	@GetMapping("/notifications/{doctorId}")
	public ResponseEntity<ResultResponse<List<NotificationDto>>> getDoctorNotificationsByDoctorId(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String doctorId) {
		// Ensure the logged-in user can only update their own doctor details
		if (!userId.equals(doctorId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
		}
		return doctorService.getDoctorNotifications(doctorId);
	}

	// markDoctorNotificationAsRead
	@PutMapping("/notifications/markAsRead/{notificationId}")
	public ResponseEntity<ResultResponse<Void>> markDoctorNotificationAsRead(
			@Valid @PathVariable String notificationId) {
		return doctorService.markDoctorNotificationAsRead(notificationId);
	}

	// ----------------------------------------------------------------------------------------------------------------------------------

	// filterPatientMedicalDetailsBYDateRangeAndPatientId -->9/4/2025
	@GetMapping("/filterByDate/{startDate}/{endDate}/{patientId}")
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> filterPatientMedicalDetailsByDateRange(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String startDate,
			@Valid @PathVariable String endDate, @Valid @PathVariable String patientId) {

//		// Ensure the logged-in user can only update their own PATIENT details
//		if (!userId.equals(patientId)) {
//			log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
//			throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
//		}

		return doctorService.filterPatientMedicalRecordsByDateRange(startDate, endDate, patientId);
	}

	// filterDoctorAppointmentsByDateRange -->9/4/2025
	@GetMapping("/filterAppointmentsByDate/{startDate}/{endDate}/{doctorId}/{appointmentStatus}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> filterDoctorAppointmentsByDateRange(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String startDate,
			@Valid @PathVariable String endDate, @Valid @PathVariable String doctorId,
			@Valid @PathVariable String appointmentStatus) {

		// Ensure the logged-in user can only update their own doctor details
		if (!userId.equals(doctorId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
		}

		List<AppointmentDetailsDto> appointments = doctorService.filterDoctorAppointmentByDateRange(startDate, endDate,
				doctorId, appointmentStatus);
		ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
				.success(true).data(appointments)
				.message("Successfully retrieved doctor appointments within date range").timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
