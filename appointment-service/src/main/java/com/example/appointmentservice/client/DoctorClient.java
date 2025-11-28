package com.example.appointmentservice.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.appointmentservice.dto.DoctorAvailabilityDto;
import com.example.appointmentservice.dto.DoctorDto;
import com.example.appointmentservice.dto.SpecializationDto;
import com.example.appointmentservice.enums.Session;
import com.example.appointmentservice.model.ResultResponse;
import com.example.appointmentservice.security.FeignClientConfiguration;

import jakarta.validation.Valid;

@FeignClient(name = "DOCTOR-SERVICE", configuration = FeignClientConfiguration.class)
public interface DoctorClient {

	// fetchDoctorById
	@GetMapping("/api/doctors/{doctorId}")
	public ResponseEntity<ResultResponse<DoctorDto>> fetchDoctorById(@Valid @PathVariable String doctorId);

	// isDoctorAvailableByDoctorIdDateAndSession
	@GetMapping("/api/doctorAvailability/availability/check")
	public ResponseEntity<ResultResponse<Integer>> isDoctorAvailable(@Valid @PathVariable String doctorId,
			@Valid @PathVariable LocalDate date, @Valid @PathVariable Session session);

	// findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailableForBookAndRescheduleAppointment
	@GetMapping("/api/doctorAvailability/availableDoctors/{doctorId}/{appointmentDate}/{session}/{isAvailable}")
	public ResponseEntity<ResultResponse<Optional<DoctorAvailabilityDto>>> findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailable(
			@Valid @PathVariable String doctorId, @Valid @PathVariable String appointmentDate,
			@Valid @PathVariable Session session, @Valid @PathVariable int isAvailable);

	// addDoctorAvailability
	@PostMapping("/api/doctorAvailability/add")
	public ResponseEntity<ResultResponse<DoctorAvailabilityDto>> addDoctorAvailability(
			@Valid @RequestBody DoctorAvailabilityDto daDto);

	// findDoctorAvailabilityById
	@GetMapping("/api/doctorAvailability/{availabilityId}")
	public ResponseEntity<ResultResponse<DoctorAvailabilityDto>> findAvailabilityById(
			@Valid @PathVariable String availabilityId);

	// findSpecializationById
	@GetMapping("/specialization/id/{specId}")
	public ResponseEntity<ResultResponse<SpecializationDto>> findById(@Valid @PathVariable String specId);

	// findDoctorsBySpecializationName
	@GetMapping("/api/doctors/specialization/name/{specializationName}")
	public ResponseEntity<ResultResponse<List<DoctorDto>>> getDoctorsListBySpecializationName(
			@Valid @PathVariable String specializationName);

}
