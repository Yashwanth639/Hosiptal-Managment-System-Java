package com.example.appointmentservice.controller;

import com.example.appointmentservice.dto.AppointmentDetailsDto;
import com.example.appointmentservice.dto.BookAppointmentDto;
import com.example.appointmentservice.dto.RescheduleRequestDto;
import com.example.appointmentservice.dto.SendReminderDto;
import com.example.appointmentservice.exception.UnauthorizedAccessException;
import com.example.appointmentservice.model.ResultResponse;
import com.example.appointmentservice.service.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing appointment-related operations in a microservice
 * architecture.
 */
@Slf4j
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AppointmentController {

	@Autowired
	private AppointmentService appointmentService;

	// CRUD backend admin operations

	// getAllAppointments
	@GetMapping("/getAll")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> findAll() {
		List<AppointmentDetailsDto> appointments = appointmentService.findAllAppointments();
		log.info("Successfully retrieved all appointments.");

		ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
				.data(appointments).success(true).message("Appointments retrieved successfully")
				.timestamp(LocalDateTime.now()).build();

		return ResponseEntity.ok(response);
	}

	// getAppointmentById
	@GetMapping("/{appointmentId}")
	public ResponseEntity<ResultResponse<AppointmentDetailsDto>> findById(@Valid @PathVariable String appointmentId) {
		AppointmentDetailsDto appointment = appointmentService.findById(appointmentId);
		log.info("Successfully retrieved appointment with ID: {}", appointmentId);

		ResultResponse<AppointmentDetailsDto> response = ResultResponse.<AppointmentDetailsDto>builder()
				.data(appointment).success(true).message("Appointment retrieved successfully")
				.timestamp(LocalDateTime.now()).build();

		return ResponseEntity.ok(response);
	}

	// addAppointment
	@PostMapping("/add")
	public ResponseEntity<ResultResponse<BookAppointmentDto>> addAppointment(
			@Valid @RequestBody BookAppointmentDto appointmentDto) {
		BookAppointmentDto appointment = appointmentService.addAppointment(appointmentDto);
		log.info("Added appointment with ID: {}", appointment.getAppointmentId());

		ResultResponse<BookAppointmentDto> response = ResultResponse.<BookAppointmentDto>builder().data(appointment)
				.success(true).message("Appointment added successfully").timestamp(LocalDateTime.now()).build();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// updateAppointment
	@PutMapping("/update/{appointmentId}")
	public ResponseEntity<ResultResponse<AppointmentDetailsDto>> updateAppointment(
			@Valid @PathVariable String appointmentId, @Valid @RequestBody AppointmentDetailsDto appointmentDto) {
		AppointmentDetailsDto appointment = appointmentService.updateAppointment(appointmentId, appointmentDto);
		log.info("Successfully updated appointment with ID: {}", appointmentId);
		
		ResultResponse<AppointmentDetailsDto> response = ResultResponse.<AppointmentDetailsDto>builder()
				.data(appointment).success(true).message("Appointment updated successfully")
				.timestamp(LocalDateTime.now()).build();

		return ResponseEntity.ok(response);
	}

	// deleteAppointmentById
	@DeleteMapping("/delete/{appointmentId}")
	public ResponseEntity<ResultResponse<Void>> deleteById(@Valid @PathVariable String appointmentId) {
		appointmentService.deleteById(appointmentId);
		log.info("Successfully deleted appointment with ID: {}", appointmentId);

		ResultResponse<Void> response = ResultResponse.<Void>builder().success(true)
				.message("Appointment deleted successfully").data(null).timestamp(LocalDateTime.now()).build();

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
	}

	// deleteAllAppointments
	@DeleteMapping("/deleteAll")
	public ResponseEntity<ResultResponse<Void>> deleteAllAppointment() {
		appointmentService.deleteAllAppointment();
		log.info("Successfully deleted all appointments.");

		ResultResponse<Void> response = ResultResponse.<Void>builder().success(true)
				.message("All appointments deleted successfully").data(null).timestamp(LocalDateTime.now()).build();

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Gets current appointments for a patient.
	 *
	 * Takes patient ID, returns list of current appointments.
	 */
	// getCurrentPatientAppointments
	@GetMapping("/current/patient/{patientId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getCurrentPatientAppointments(
			@RequestAttribute("userId") String userId,@Valid @PathVariable String patientId) {
		
		// Ensure the logged-in user can only update their own patient details
	    if (!userId.equals(patientId)) {
	        log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
	        throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
	    }
		
		
		
		List<AppointmentDetailsDto> currentPatientAppointments = appointmentService
				.getCurrentPatientAppointments(patientId);
		ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
				.data(currentPatientAppointments).success(true).timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// getCurrentDoctorAppointments
	@GetMapping("/current/doctor/{doctorId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getCurrentDoctorAppointments(@RequestAttribute("userId") String userId,
			@Valid @PathVariable String doctorId) {
		
		// Ensure the logged-in user can only update their own doctor details
	    if (!userId.equals(doctorId)) {
	        log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
	        throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
	    }
		
		List<AppointmentDetailsDto> currentDoctorAppointments = appointmentService
				.getCurrentDoctorAppointments(doctorId);
		ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
				.data(currentDoctorAppointments).success(true).timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Gets past appointments for a patient.
	 *
	 * Takes patient ID, returns list of past appointments.
	 */
	// getPastPatientAppointments
	@GetMapping("/past/patient/{patientId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getPastPatientAppointments(@RequestAttribute("userId") String userId,
			@Valid @PathVariable String patientId) {
		
		// Ensure the logged-in user can only update their own patient details
	    if (!userId.equals(patientId)) {
	        log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
	        throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
	    }
		
		
		List<AppointmentDetailsDto> pastPatientAppointments = appointmentService.getPastPatientAppointments(patientId);
		ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
				.data(pastPatientAppointments).success(true).timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// getPastDoctorAppointments
	@GetMapping("/past/doctor/{doctorId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getPastDoctorAppointments(@RequestAttribute("userId") String userId,
			@Valid @PathVariable String doctorId) {
		
		// Ensure the logged-in user can only update their own doctor details
	    if (!userId.equals(doctorId)) {
	        log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
	        throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
	    }
	    
		List<AppointmentDetailsDto> pastDoctorAppointments = appointmentService.getPastDoctorAppointments(doctorId);
		ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
				.data(pastDoctorAppointments).success(true).timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Books a new appointment.
	 *
	 * Takes appointment info, returns booking result.
	 */
	// bookAppointment
	@PostMapping("/book")
	public ResponseEntity<ResultResponse<BookAppointmentDto>> bookAppointment(
			@Valid @RequestBody BookAppointmentDto requestDto) {
		log.debug("Received booking request: {}", requestDto);
		BookAppointmentDto bookedAppointment = appointmentService.bookAppointment(requestDto);
		ResultResponse<BookAppointmentDto> response = ResultResponse.<BookAppointmentDto>builder()
				.data(bookedAppointment).success(true).timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Reschedules an existing appointment.
	 *
	 * Takes appointment ID and new details, returns updated booking info.
	 */
	// rescheduleAppointment
	@PostMapping("/reschedule")
	public ResponseEntity<ResultResponse<RescheduleRequestDto>> rescheduleAppointment(
			@Valid @RequestBody RescheduleRequestDto requestDto) {
		log.info("Successfully rescheduled appointment with ID: {}", requestDto.getAppointmentId());
		RescheduleRequestDto rescheduledAppointment = appointmentService.rescheduleAppointment(requestDto);
		ResultResponse<RescheduleRequestDto> response = ResultResponse.<RescheduleRequestDto>builder()
				.data(rescheduledAppointment).success(true).timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Cancels an existing appointment.
	 *
	 * Takes appointment ID, returns cancellation status.
	 */
	// cancelAppointment
	@PostMapping("/cancel/{appointmentId}")
	public ResponseEntity<ResultResponse<Void>> cancelAppointment(@Valid @PathVariable String appointmentId) {
		log.info("Canceled the appointment with ID: {}", appointmentId);
		appointmentService.cancelAppointment(appointmentId);
		ResultResponse<Void> response = ResultResponse.<Void>builder().success(true)
				.message("Appointment successfully canceled.").timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------

	// findAppointmentsByAppointmentDate
	@GetMapping("/date/{appointmentDate}")
	public ResponseEntity<ResultResponse<List<SendReminderDto>>> findAppointmentsByAppointmentDate(
			@Valid @PathVariable LocalDate appointmentDate) {
		log.info("Getting appointments with current date : {}", appointmentDate);
		List<SendReminderDto> appointments = appointmentService.findAppointmentsByAppointmentDate(appointmentDate);
		ResultResponse<List<SendReminderDto>> response = ResultResponse.<List<SendReminderDto>>builder()
				.data(appointments).success(true).message("Appointments successfully retrieved by appointmentDate")
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------

	// completeAppointment
	@PostMapping("/completeAppointment/{appointmentId}")
	public void completeAppointment(@Valid @PathVariable String appointmentId) {
		appointmentService.completeAppointment(appointmentId);
	}

	// findAppointmentByPatientId
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> findByPatientId(@RequestAttribute("userId") String userId,
			@Valid @PathVariable String patientId) {
		
//		// Ensure the logged-in user can only update their own patient details
//	    if (!userId.equals(patientId)) {
//	        log.warn("Unauthorized access: User ID {} attempted to update Patient ID {}", userId, patientId);
//	        throw new UnauthorizedAccessException("You are not authorized to update this patient's details.");
//	    }
//	    
		List<AppointmentDetailsDto> list = appointmentService.findByPatientId(patientId);
		ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
				.data(list).message("Successfully retrieved patient's all appointments").success(true)
				.timestamp(LocalDateTime.now()).build();
		log.info("Successfully retrieved patient's all appointments");
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// findAppointmentByDoctorId
	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> findByDoctorId(
			@RequestAttribute("userId") String userId,
			@Valid @PathVariable String doctorId) {
		
		// Ensure the logged-in user can only update their own doctor details
	    if (!userId.equals(doctorId)) {
	        log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
	        throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
	    }
	    
		List<AppointmentDetailsDto> list = appointmentService.findByDoctorId(doctorId);
		ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
				.data(list).message("Successfully retrieved doctor's all appointments").success(true)
				.timestamp(LocalDateTime.now()).build();
		log.info("Successfully retrieved doctor's all appointments");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------
	
	// filterAppointmentsUsingDateRange --> 9/4/2025
		@GetMapping("/filter/{startDate}/{endDate}")
		public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> filterAppointmentsByDateRange(@Valid @PathVariable LocalDate startDate, @Valid @PathVariable LocalDate endDate){
			List<AppointmentDetailsDto> list = appointmentService.findAppointmentsWithinDateRange(startDate, endDate);
			ResultResponse<List<AppointmentDetailsDto>> response = ResultResponse.<List<AppointmentDetailsDto>>builder()
					.data(list).message("Successfully retrieved appointments within date range").success(true)
					.timestamp(LocalDateTime.now()).build();
			log.info("Successfully retrieved appointments within date range");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

}