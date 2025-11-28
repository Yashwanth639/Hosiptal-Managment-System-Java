package com.example.doctorservice.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doctorservice.dto.DoctorAvailabilityDto;

import com.example.doctorservice.enums.Session;
import com.example.doctorservice.exceptions.UnauthorizedAccessException;
import com.example.doctorservice.model.ResultResponse;
import com.example.doctorservice.service.DoctorAvailabilityService;
import com.example.doctorservice.service.DoctorService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/doctorAvailability")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorAvailabilityController {

	@Autowired
	private DoctorAvailabilityService daService;

	@Autowired
	private DoctorService doctorService;

	// getAllDoctorAvailability
	@GetMapping("/getAll")
	public ResponseEntity<ResultResponse<List<DoctorAvailabilityDto>>> findAll() {
		List<DoctorAvailabilityDto> dAs = daService.findAll();
		ResultResponse<List<DoctorAvailabilityDto>> response = ResultResponse.<List<DoctorAvailabilityDto>>builder()
				.data(dAs).success(true).message("Successfully retrieved all doctor availabilities")
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// getDoctorAvailabilityById
	@GetMapping("/{availabilityId}")
	public ResponseEntity<ResultResponse<DoctorAvailabilityDto>> findById(@Valid @PathVariable String availabilityId) {
		DoctorAvailabilityDto doctorAvailability = daService.findById(availabilityId);
		ResultResponse<DoctorAvailabilityDto> response = ResultResponse.<DoctorAvailabilityDto>builder()
				.data(doctorAvailability).success(true)
				.message("Successfully retrieved doctor availability with id : " + availabilityId)
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// addDoctorAvailability
	@PostMapping("/add")
	public ResponseEntity<ResultResponse<DoctorAvailabilityDto>> addDoctorAvailability(
			@Valid @RequestBody DoctorAvailabilityDto daDto) {
		DoctorAvailabilityDto record = daService.addDoctorAvailability(daDto);
		ResultResponse<DoctorAvailabilityDto> response = ResultResponse.<DoctorAvailabilityDto>builder().data(record)
				.success(true).message("Doctor Availability Record added successfully").timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/id/{availabilityId}")
	public ResponseEntity<ResultResponse<DoctorAvailabilityDto>> findAvailabilityById(
			@Valid @PathVariable String availabilityId) {
		DoctorAvailabilityDto record = daService.findById(availabilityId);
		ResultResponse<DoctorAvailabilityDto> response = ResultResponse.<DoctorAvailabilityDto>builder().data(record)
				.success(true).message("Successfully retrieved doctor availability with id : " + availabilityId)
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// updateDoctorAvailabilityById
	@PutMapping("/update/{daId}")
	public ResponseEntity<ResultResponse<DoctorAvailabilityDto>> updateDoctorAvailability(
			@Valid @PathVariable String daId, @Valid @RequestBody DoctorAvailabilityDto daDto) {
		DoctorAvailabilityDto doctorAvailability = daService.updateDoctorAvailability(daId, daDto);
		ResultResponse<DoctorAvailabilityDto> response = ResultResponse.<DoctorAvailabilityDto>builder()
				.data(doctorAvailability).success(true).message("Updated doctor availability with id : " + daId)
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// deleteDoctorAvailabilityById
	@DeleteMapping("/delete/{daId}")
	public ResponseEntity<ResultResponse<Void>> deleteById(@Valid @PathVariable String daId) {
		daService.deleteById(daId);
		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("Doctor availability records deleted successfully").data(null).timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resultResponse);
	}

	// deleteAllDoctorAvailability
	@DeleteMapping("/deleteAll")
	public ResponseEntity<ResultResponse<Void>> deleteAll() {
		daService.deleteAll();
		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("All doctor availability records deleted successfully").data(null)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resultResponse);
	}

	// ---------------------------------------------------------------------------------------------------------------------------

	// getDoctorScheduleById
	@GetMapping("/getSchedule/{doctorId}")
	ResponseEntity<ResultResponse<List<DoctorAvailabilityDto>>> getDoctorSchedule(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String doctorId) {

//		// Ensure the logged-in user can only update their own doctor details
//		if (!userId.equals(doctorId)) {
//			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
//			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
//		}

		List<DoctorAvailabilityDto> schedule = daService.getDoctorSchedule(doctorId);
		ResultResponse<List<DoctorAvailabilityDto>> resultResponse = ResultResponse
				.<List<DoctorAvailabilityDto>>builder().success(true).message("Retrieved doctor schedule")
				.data(schedule).timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
	}

	// findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailable
	@GetMapping("/availableDoctors/{doctorId}/{appointmentDate}/{session}/{isAvailable}")
	public ResponseEntity<ResultResponse<Optional<DoctorAvailabilityDto>>> findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailable(
			@RequestAttribute("userId") String userId, @Valid @PathVariable String doctorId,
			@Valid @PathVariable LocalDate appointmentDate, @Valid @PathVariable Session session,
			@Valid @PathVariable int isAvailable) {

//		// Ensure the logged-in user can only update their own doctor details
//		if (!userId.equals(doctorId)) {
//			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
//			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
//		}

		Optional<DoctorAvailabilityDto> optional = daService
				.findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailable(doctorId, appointmentDate,
						session, isAvailable);
		ResultResponse<Optional<DoctorAvailabilityDto>> response = ResultResponse
				.<Optional<DoctorAvailabilityDto>>builder().data(optional).success(true)
				.message(
						"Doctor availability records retrieved successfully by Doctor ID, Appointment Date, Session and Is Available")
				.timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// isDoctorAvailable
	@GetMapping("/availability/check")
	public ResponseEntity<ResultResponse<Integer>> isDoctorAvailable(

			@Valid @PathVariable String doctorId, @Valid @PathVariable LocalDate date,
			@Valid @PathVariable Session session) {

		log.info("Received request to check availability for doctor ID: {}, date: {}, session: {}", doctorId, date,
				session);

		// Check availability of the doctor
		int isAvailable = doctorService.isDoctorAvailable(doctorId, date, session);

		// Build success response
		String message = isAvailable == 1 ? "Doctor is available." : "Doctor is not available.";
		ResultResponse<Integer> response = ResultResponse.<Integer>builder().success(true).message(message)
				.data(isAvailable).timestamp(LocalDateTime.now()).build();

		log.info("Doctor availability check completed for doctor ID: {}, date: {}, session: {}. Availability: {}",
				doctorId, date, session, isAvailable);
		return ResponseEntity.ok(response);

	}

	// -----------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * REST endpoint to fetch available doctors.
	 *
	 * @param specializationName The name of the specialization.
	 * @param availableDate      The date to check availability.
	 * @param session            The session (FN or AN) to check availability.
	 * @return A ResponseEntity containing a list of DoctorAvailabilityDto objects.
	 */
	// To obtain the doctor for successful booking and rescheduling of appointments
	@GetMapping("/availableDoctors/{specializationName}/{availableDate}/{session}")
	public ResponseEntity<List<DoctorAvailabilityDto>> getAvailableDoctors(
			@Valid @PathVariable String specializationName, @Valid @PathVariable LocalDate availableDate,
			@Valid @PathVariable Session session) {
		List<DoctorAvailabilityDto> doctors = daService.getAvailableDoctors(specializationName, availableDate, session);
		return ResponseEntity.ok(doctors);
	}

	// updateDoctorAvailabilitySlotStatus
	@PutMapping("/toggle/{availabilityId}")
	public ResponseEntity<ResultResponse<Void>> updateDoctorAvailabilitySlotStatus(
			@Valid @PathVariable String availabilityId) {
		daService.updateAvailabilitySlotStatus(availabilityId);
		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("Doctor successfully updated their schedule").data(null).timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resultResponse);
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// getDoctorAvailabilityWithinDateRangeByDoctorId --> 9/4/25
	@GetMapping("/filterSchedule/{startDate}/{endDate}/{doctorId}")
	public ResponseEntity<ResultResponse<List<DoctorAvailabilityDto>>> filterDoctorSchedule(
			@RequestAttribute("userId") String userId, @Valid @PathVariable LocalDate startDate,
			@Valid @PathVariable LocalDate endDate, @Valid @PathVariable String doctorId) {

		// Ensure the logged-in user can only update their own doctor details
		if (!userId.equals(doctorId)) {
			log.warn("Unauthorized access: User ID {} attempted to update Doctor ID {}", userId, doctorId);
			throw new UnauthorizedAccessException("You are not authorized to update this doctor's details.");
		}

		List<DoctorAvailabilityDto> schedule = daService.getDoctorAvailabilityByDateRange(startDate, endDate, doctorId);
		ResultResponse<List<DoctorAvailabilityDto>> response = ResultResponse.<List<DoctorAvailabilityDto>>builder()
				.success(true).message("Successfully retrieved filtered doctor schedule within date range")
				.data(schedule).timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
