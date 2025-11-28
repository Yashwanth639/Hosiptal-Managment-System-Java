package com.example.appointmentservice.service;

import com.example.appointmentservice.repository.AppointmentRepository;
import com.example.appointmentservice.client.DoctorClient;
import com.example.appointmentservice.client.NotificationClient;
import com.example.appointmentservice.client.PatientClient;
import com.example.appointmentservice.dto.AppointmentDetailsDto;
import com.example.appointmentservice.dto.BookAppointmentAndCancelNotificationDto;
import com.example.appointmentservice.dto.BookAppointmentDto;
import com.example.appointmentservice.dto.DoctorAvailabilityDto;
import com.example.appointmentservice.dto.DoctorDto;
import com.example.appointmentservice.dto.PatientDto;

import com.example.appointmentservice.dto.RescheduleNotificationDto;
import com.example.appointmentservice.dto.RescheduleRequestDto;
import com.example.appointmentservice.dto.SendReminderDto;
import com.example.appointmentservice.dto.SpecializationDto;
import com.example.appointmentservice.enums.AppointmentStatus;

import com.example.appointmentservice.exception.*;
import com.example.appointmentservice.model.Appointment;

import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class that manages the core logic for appointments. It interacts with
 * the data access layer (repositories) to perform operations such as booking,
 * retrieving, canceling, and rescheduling appointments. This class also handles
 * business logic validation and exception handling.
 */
@Service
@Slf4j

public class AppointmentService {

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private NotificationClient notificationClient;

	@Autowired
	private DoctorClient doctorClient;

	@Autowired
	private PatientClient patientClient;

	/**
	 * Converts an {@link Appointment} entity to an {@link AppointmentDetailsDto}.
	 * This method transforms the entity into a format suitable for transferring
	 * data to the client.
	 *
	 * @param appointment The {@link Appointment} entity to convert.
	 * @return An {@link AppointmentDetailsDto} representing the appointment
	 *         details.
	 */

	// convertToAppointmentDetailsDto
	private AppointmentDetailsDto convertToAppointmentDetailsDto(Appointment appointment) {
		// You will fetch doctor and patient names from other services later
		DoctorDto doctor = doctorClient.fetchDoctorById(appointment.getDoctorId()).getBody().getData();
		String specializationId = doctor.getSpecializationId();
		String doctorName = doctor.getName();

		SpecializationDto sDto = doctorClient.findById(specializationId).getBody().getData();
		String specializationName = sDto.getSpecializationName();

		PatientDto patient = patientClient.fetchPatientById(appointment.getPatientId()).getBody().getData();
		String patientName = patient.getName();

		return AppointmentDetailsDto.builder().appointmentId(appointment.getAppointmentId())
				.appointmentDate(appointment.getAppointmentDate()).doctorId(appointment.getDoctorId()) // Placeholder
				.patientId(appointment.getPatientId()).session(appointment.getSession())
				.specializationId(specializationId) // Placeholder
				.status(appointment.getAppointmentstatus()).doctorName(doctorName).patientName(patientName)
				.specializationName(specializationName).build();

	}

	// convertToSendReminderDto
	private SendReminderDto convertToSendReminderDto(Appointment appointment) {
		PatientDto patient = patientClient.fetchPatientById(appointment.getPatientId()).getBody().getData();
		String patientName = patient.getName();

		DoctorDto doctor = doctorClient.fetchDoctorById(appointment.getDoctorId()).getBody().getData();
		String doctorName = doctor.getName();

		return SendReminderDto.builder().appointmentId(appointment.getAppointmentId())
				.patientId(appointment.getPatientId()).patientName(patientName).doctorName(doctorName)
				.doctorId(appointment.getDoctorId()).session(appointment.getSession())
				.status(appointment.getAppointmentstatus()).build();
	}

	// convertEntityToDTO
	private BookAppointmentDto convertToDTO(Appointment appointment) {
		BookAppointmentDto appointmentDto = new BookAppointmentDto();

		appointmentDto.setAppointmentId(appointment.getAppointmentId());
		appointmentDto.setAvailabilityId(appointment.getAvailabilityId());
		appointmentDto.setDoctorId(appointment.getDoctorId());
		appointmentDto.setPatientId(appointment.getPatientId());
		appointmentDto.setAppointmentDate(appointment.getAppointmentDate());
		appointmentDto.setSession(appointment.getSession());
		appointmentDto.setStatus(appointment.getAppointmentstatus());

		PatientDto patient = patientClient.fetchPatientById(appointment.getPatientId()).getBody().getData();
		String patientName = patient.getName();

		DoctorDto doctor = doctorClient.fetchDoctorById(appointment.getDoctorId()).getBody().getData();
		String doctorName = doctor.getName();

		SpecializationDto specialization = doctorClient.findById(doctor.getSpecializationId()).getBody().getData();
		String specializationName = specialization.getSpecializationName();

		appointmentDto.setPatientName(patientName);
		//appointmentDto.setDoctorName(doctorName);
		appointmentDto.setSpecializationName(specializationName);

		return appointmentDto;
	}

	// convertDTOToEntity
	private Appointment convertToEntity(BookAppointmentDto appointmentDto) {
		Appointment appointment = new Appointment();

		appointment.setCreatedAt(LocalDateTime.now());
		appointment.setUpdatedAt(LocalDateTime.now());
		appointment.setAppointmentId(UUID.randomUUID().toString());
		appointment.setAvailabilityId(appointmentDto.getAvailabilityId());
		appointment.setDoctorId(appointmentDto.getDoctorId());

		appointment.setPatientId(appointmentDto.getPatientId());

		appointment.setAppointmentDate(appointmentDto.getAppointmentDate());
		appointment.setSession(appointmentDto.getSession());
		appointment.setAppointmentstatus(appointmentDto.getStatus());

		return appointment;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------

	// findAllAppointments
	public List<AppointmentDetailsDto> findAllAppointments() {
		List<AppointmentDetailsDto> result = new ArrayList<>();

		List<Appointment> appointments = appointmentRepository.findAll();

		if (appointments.isEmpty()) {
			log.warn("No appointments found.");
			return result; // Return an empty list
		}

		for (Appointment appointment : appointments) {
			AppointmentDetailsDto appointmentDto = convertToAppointmentDetailsDto(appointment);
			result.add(appointmentDto);
		}

		log.info("Successfully retrieved all appointments.");
		return result;
	}

	// findAppointmentById
	public AppointmentDetailsDto findById(String appointmentId) {

		List<Appointment> appointments = appointmentRepository.findAll();
		for (Appointment appointment : appointments) {
			if (appointment.getAppointmentId().equals(appointmentId)) {
				AppointmentDetailsDto appointmentDto = convertToAppointmentDetailsDto(appointment);
				log.info("Successfully retrieved appointment with ID: {}", appointmentId);
				return appointmentDto;
			}
		}
		log.warn("Appointment with ID: {} not found.", appointmentId);
		throw new AppointmentNotFoundException("Appointment not found with id : " + appointmentId);

	}

	// addAppointment
	public BookAppointmentDto addAppointment(BookAppointmentDto appointmentDto) {

		PatientDto patient = patientClient.fetchPatientById(appointmentDto.getPatientId()).getBody().getData();
		String patientName = patient.getName();

		DoctorDto doctor = doctorClient.fetchDoctorById(appointmentDto.getDoctorId()).getBody().getData();
		String doctorName = doctor.getName();

		Appointment appointment = convertToEntity(appointmentDto);
		appointment.setAppointmentstatus(AppointmentStatus.SCHEDULED);
		Appointment savedAppointment = appointmentRepository.save(appointment);
		log.info("Successfully added appointment with ID: {}", savedAppointment.getAppointmentId());
		BookAppointmentDto convertedAppointment = convertToDTO(savedAppointment);
		convertedAppointment.setSpecializationName(appointmentDto.getSpecializationName());
		//convertedAppointment.setDoctorName(doctorName);
		convertedAppointment.setPatientName(patientName);
		return convertedAppointment;

	}

	// updateAppointment
	public AppointmentDetailsDto updateAppointment(String appointmentId, AppointmentDetailsDto appointmentDto) {

		Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);

		if (optionalAppointment.isPresent()) {
			Appointment existingAppointment = optionalAppointment.get();
			existingAppointment.setAppointmentDate(appointmentDto.getAppointmentDate());
			existingAppointment.setSession(appointmentDto.getSession());

			existingAppointment.setAppointmentstatus(appointmentDto.getStatus());
			Appointment savedAppointment = appointmentRepository.save(existingAppointment);
			log.info("Successfully updated appointment with ID: {}", appointmentId);
			return convertToAppointmentDetailsDto(savedAppointment);
		} else {
			log.warn("Appointment not found with ID: {}", appointmentId);
			throw new AppointmentNotFoundException("Appointment not found with id: " + appointmentId);
		}

	}

	// deleteAppointmentById
	public void deleteById(String appointmentId) {

		if (appointmentRepository.existsById(appointmentId)) {
			appointmentRepository.deleteById(appointmentId);
			log.info("Successfully deleted appointment with ID: {}", appointmentId);
		} else {
			log.warn("Appointment not found with ID: {}", appointmentId);
			throw new AppointmentNotFoundException("Appointment not found with id: " + appointmentId);
		}

	}

	// deleteAllAppointment
	public void deleteAllAppointment() {

		appointmentRepository.deleteAll();
		log.info("Successfully deleted all appointments.");

	}

	// ------------------------------------------------------------------------------------------------------------------------

	/**
	 * Retrieves a list of current scheduled appointments for a specific patient.
	 * Current appointments are those scheduled for today or in the future.
	 *
	 * @param patientId The unique identifier of the patient.
	 * @return A list of {@link AppointmentDetailsDto} representing the current
	 *         appointments.
	 */
	// findCurrentPatientAppointments
	public List<AppointmentDetailsDto> getCurrentPatientAppointments(String patientId) {
		LocalDate currentDate = LocalDate.now();

		return appointmentRepository.findCurrentPatientAppointments(patientId, currentDate, AppointmentStatus.SCHEDULED)
				.stream().map(this::convertToAppointmentDetailsDto).collect(Collectors.toList());
	}

	// findCurrentDoctorAppointments
	public List<AppointmentDetailsDto> getCurrentDoctorAppointments(String doctorId) {
		LocalDate currentDate = LocalDate.now();
		return appointmentRepository.findCurrentDoctorAppointments(doctorId, currentDate, AppointmentStatus.SCHEDULED)
				.stream().map(this::convertToAppointmentDetailsDto).collect(Collectors.toList());
	}

	/**
	 * Retrieves a list of past appointments for a specific patient. Past
	 * appointments are those scheduled for dates before the current date.
	 *
	 * @param patientId The unique identifier of the patient.
	 * @return A list of {@link AppointmentDetailsDto} representing the past
	 *         appointments.
	 */
	// findPastPatientAppointments
	public List<AppointmentDetailsDto> getPastPatientAppointments(String patientId) {
		LocalDate currentDate = LocalDate.now();
		System.out.println("Successfully reached..");
		return appointmentRepository.findPastPatientAppointments(patientId, currentDate, AppointmentStatus.COMPLETED)
				.stream().map(this::convertToAppointmentDetailsDto).collect(Collectors.toList());

	}

	// findPastDoctorAppointments
	public List<AppointmentDetailsDto> getPastDoctorAppointments(String doctorId) {
		LocalDate currentDate = LocalDate.now();
		return appointmentRepository.findPastDoctorAppointments(doctorId, currentDate, AppointmentStatus.COMPLETED)
				.stream().map(this::convertToAppointmentDetailsDto).collect(Collectors.toList());
	}

	// findAppointmentsByAppointmentDate
	public List<SendReminderDto> findAppointmentsByAppointmentDate(LocalDate appointmentDate) {

		List<SendReminderDto> appointments = appointmentRepository.findAppointmentsByAppointmentDate(appointmentDate)
				.stream().map(this::convertToSendReminderDto).collect(Collectors.toList());

		if (appointments.isEmpty()) {
			throw new AppointmentNotFoundException("No appointments found on " + appointmentDate);
		}

		return appointments;
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------

	// bookNewAppointment
	@Transactional
	public BookAppointmentDto bookAppointment(BookAppointmentDto bookAppointmentDTO) {

		// Check if the patient already has an appointment on the same date and session
		if (appointmentRepository.checkExistingAppointmentByPatientIdAndAppointmentDateAndSessionAndStatus(
				bookAppointmentDTO.getPatientId(), bookAppointmentDTO.getAppointmentDate(),
				bookAppointmentDTO.getSession(), AppointmentStatus.SCHEDULED)) {
			throw new IllegalArgumentException("You already have an appointment scheduled for this date and session");
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedDate = bookAppointmentDTO.getAppointmentDate().format(formatter);

		// Step 1: Find if the doctor is available in the given slot
		Optional<DoctorAvailabilityDto> optionalAvailability = doctorClient
				.findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailable(
						bookAppointmentDTO.getDoctorId(), formattedDate, bookAppointmentDTO.getSession(), 1)
				.getBody().getData();

		if (!optionalAvailability.isPresent()) {
			throw new IllegalArgumentException("Doctor is not available for the selected slot. ");
		}

		DoctorAvailabilityDto availability = optionalAvailability.get();
		bookAppointmentDTO.setAvailabilityId(availability.getAvailabilityId());
		
		SpecializationDto specialization = doctorClient.findById(availability.getSpecializationId()).getBody().getData();
		String specializationName = specialization.getSpecializationName();
		
		bookAppointmentDTO.setSpecializationName(specializationName);

		// Step 2: Generate UUID and insert appointment
		BookAppointmentDto booked = addAppointment(bookAppointmentDTO);

		// Step 3: Mark availability as unavailable
		availability.setIsAvailable(0);
		doctorClient.addDoctorAvailability(availability);

		DoctorDto doctor = doctorClient.fetchDoctorById(bookAppointmentDTO.getDoctorId()).getBody().getData();
		String doctorName = doctor.getName();

		PatientDto patient = patientClient.fetchPatientById(booked.getPatientId()).getBody().getData();
		String patientName = patient.getName();

		BookAppointmentAndCancelNotificationDto notificationDto = BookAppointmentAndCancelNotificationDto.builder()
				.appointmentId(booked.getAppointmentId()).patientId(booked.getPatientId())
				.doctorId(booked.getDoctorId()).patientName(patientName).doctorName(doctorName)
				.appointmentDate(booked.getAppointmentDate()).session(booked.getSession()).build();

		notificationClient.insertNotificationsForBookAppointment(notificationDto);

		log.info("Successfully booked appointment with ID: {}", booked.getAppointmentId());

		bookAppointmentDTO.setAppointmentId(booked.getAppointmentId());
		bookAppointmentDTO.setStatus(booked.getStatus());
		bookAppointmentDTO.setPatientName(patientName);
		//bookAppointmentDTO.setDoctorName(doctorName);

		return bookAppointmentDTO;

	}

	/**
	 * Reschedules an existing appointment to a new date and session. This method
	 * finds the existing appointment and updates its details.
	 *
	 * @param appointmentId      The unique identifier of the appointment to
	 *                           reschedule.
	 * @param newAppointmentDate The new date for the appointment.
	 * @param newSession         The new session for the appointment.
	 * @return A BookAppointmentDto representing the rescheduled appointment.
	 * @throws AppointmentNotFoundException if the appointment with the given ID is
	 *                                      not found.
	 */
	// rescheduleAppointment
	@Transactional
	public RescheduleRequestDto rescheduleAppointment(RescheduleRequestDto rescheduleAppointmentDTO) {

		// Step 1: Get current appointment details
		Appointment appointment = appointmentRepository.findById(rescheduleAppointmentDTO.getAppointmentId())
				.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found or not scheduled"));

		// Step 1.1: Check if the patient already has an appointment on the same date
		// and session
		if (appointmentRepository.checkExistingAppointmentByPatientIdAndAppointmentDateAndSessionAndStatus(
				appointment.getPatientId(), rescheduleAppointmentDTO.getNewAppointmentDate(),
				rescheduleAppointmentDTO.getNewSession(), AppointmentStatus.SCHEDULED)) {
			throw new IllegalArgumentException("You already have an appointment scheduled for this date and session");
		}

		RescheduleNotificationDto notificationDto = new RescheduleNotificationDto();
		notificationDto.setOldDate(appointment.getAppointmentDate());
		notificationDto.setOldSession(appointment.getSession());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedDate = rescheduleAppointmentDTO.getNewAppointmentDate().format(formatter);

		// Step 2: Find a new availability slot
		Optional<DoctorAvailabilityDto> newAvailabilityOpt = doctorClient
				.findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailable(
						rescheduleAppointmentDTO.getDoctorId(), formattedDate, rescheduleAppointmentDTO.getNewSession(),
						1)
				.getBody().getData();

		if (!newAvailabilityOpt.isPresent()) {
			throw new IllegalArgumentException("No available slots for the selected date and session");
		}

		DoctorAvailabilityDto newAvailability = newAvailabilityOpt.get();

		// Step 3: Restore old availability
		String oldAvailabilityId = appointment.getAvailabilityId();
		log.info("Fetching old availability with ID: {}", oldAvailabilityId);

		DoctorAvailabilityDto oldAvailability = doctorClient.findAvailabilityById(oldAvailabilityId).getBody()
				.getData();
		if (oldAvailability == null) {
			log.warn("Old availability not found for ID: {}", oldAvailabilityId); // Warn instead of throwing an error
		} else {
			oldAvailability.setIsAvailable(1);
			log.info("Old availability with ID {} restored successfully.", oldAvailabilityId);
			doctorClient.addDoctorAvailability(oldAvailability);
		}
		
		LocalDate oldAppointmentDate = oldAvailability.getAvailableDate();
		String oldSession = oldAvailability.getSession();

		// Step 4: Update the appointment
		appointment.setDoctorId(rescheduleAppointmentDTO.getDoctorId());
		appointment.setAppointmentDate(rescheduleAppointmentDTO.getNewAppointmentDate());
		appointment.setSession(rescheduleAppointmentDTO.getNewSession());
		appointment.setAvailabilityId(newAvailability.getAvailabilityId()); // Update to the new availability ID
		appointment.setUpdatedAt(LocalDateTime.now());
		appointment.setAppointmentstatus(AppointmentStatus.SCHEDULED);

		appointmentRepository.save(appointment);

		log.info("Successfully rescheduled appointment ID {} to new date {}, session {}.",
				rescheduleAppointmentDTO.getAppointmentId(), rescheduleAppointmentDTO.getNewAppointmentDate(),
				rescheduleAppointmentDTO.getNewSession());

		// Step 5: Mark new availability as unavailable
		newAvailability.setIsAvailable(0);
		doctorClient.addDoctorAvailability(newAvailability);

		// Step 6: Retrieve user and doctor details
		DoctorDto doctor = doctorClient.fetchDoctorById(appointment.getDoctorId()).getBody().getData();
		String doctorName = doctor.getName();

		PatientDto patient = patientClient.fetchPatientById(appointment.getPatientId()).getBody().getData();
		String patientName = patient.getName();

		rescheduleAppointmentDTO.setOldAvailabilityId(oldAvailabilityId);
		rescheduleAppointmentDTO.setNewAvailabilityId(newAvailability.getAvailabilityId());
		rescheduleAppointmentDTO.setStatus(appointment.getAppointmentstatus());
		rescheduleAppointmentDTO.setDoctorName(doctorName);
		rescheduleAppointmentDTO.setPatientId(appointment.getPatientId());
		rescheduleAppointmentDTO.setDoctorName(doctorName);
		rescheduleAppointmentDTO.setOldAppointmentDate(oldAppointmentDate);
		rescheduleAppointmentDTO.setOldSession(oldSession);
		
		// Step 7: Insert notifications for both patient and doctor
		notificationDto.setAppointmentId(appointment.getAppointmentId());
		notificationDto.setPatientId(appointment.getPatientId());
		notificationDto.setDoctorId(appointment.getDoctorId());
		notificationDto.setPatientName(patientName);
		notificationDto.setDoctorName(doctorName);
		notificationDto.setNewDate(appointment.getAppointmentDate());
		notificationDto.setNewSession(appointment.getSession());

		notificationClient.insertNotificationsForRescheduleAppointment(notificationDto);

		log.info("Notification sent for rescheduling appointment ID: {}", appointment.getAppointmentId());
		return rescheduleAppointmentDTO;
	}

	// cancelAppointment
	@Transactional
	public void cancelAppointment(String appointmentId) {

		// Step 1: Get appointment details
		Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(
				() -> new AppointmentNotFoundException("Appointment not found with id : " + appointmentId));

		if (!appointment.getAppointmentstatus().equals(AppointmentStatus.SCHEDULED)) {
			throw new AppointmentNotFoundException("Only appointments with status Scheduled can be cancelled");
		}

		LocalDate currentDate = LocalDate.now();
		if (appointment.getAppointmentDate().isBefore(currentDate)) {
			throw new AppointmentNotFoundException("Appointments with past dates cannot be cancelled");
		}

		String availabilityId = appointment.getAvailabilityId();
		DoctorAvailabilityDto availability = doctorClient.findAvailabilityById(availabilityId).getBody().getData();

		String doctorId = appointment.getDoctorId();
		DoctorDto doctor = doctorClient.fetchDoctorById(doctorId).getBody().getData();
		String doctorName = doctor.getName();

		String patientId = appointment.getPatientId();
		PatientDto patient = patientClient.fetchPatientById(patientId).getBody().getData();
		String patientName = patient.getName();

		// Step 2: Update appointment status to 'Cancelled'
		appointment.setAppointmentstatus(AppointmentStatus.CANCELLED);
		appointmentRepository.save(appointment);

		// Step 3: Restore availability
		availability.setIsAvailable(1);
		doctorClient.addDoctorAvailability(availability);

		// Create the notification DTO
		BookAppointmentAndCancelNotificationDto notificationDto = BookAppointmentAndCancelNotificationDto.builder()
				.appointmentId(appointment.getAppointmentId()).patientId(appointment.getPatientId())
				.doctorId(appointment.getDoctorId()).patientName(patientName).doctorName(doctorName)
				.appointmentDate(appointment.getAppointmentDate()).session(appointment.getSession()).build();

		notificationClient.insertNotificationsForCancelAppointment(notificationDto);

		log.info("Successfully cancelled appointment with ID: {}", appointment.getAppointmentId());

	}

	// -------------------------------------------------------------------------------------------------------------------------------------

	// completeAppointment
	@Transactional
	public void completeAppointment(String appointmentId) {
		log.info("Marking appointment with ID: {} as completed", appointmentId);

		Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
		if (!optionalAppointment.isPresent()) {
			throw new AppointmentNotFoundException("Appointment not found with the given Id");
		}

		Appointment appointment = optionalAppointment.get();

		if (!(appointment.getAppointmentstatus() == AppointmentStatus.SCHEDULED)) {
			throw new AppointmentAlreadyCancelledException(
					"Appointments already cancelled or completed cannot be marked as Completed again");
		}

		LocalDate currentDate = LocalDate.now();

		if ((appointment.getAppointmentDate().isAfter(currentDate))
				|| (appointment.getAppointmentDate().isBefore(currentDate))) {
			throw new AppointmentNotFoundException(
					"Future or Past Appointments cannot be marked as completed. They can be only marked as completed on the date of appointment");
		}

		appointment.setAppointmentstatus(AppointmentStatus.COMPLETED);
		appointmentRepository.save(appointment);

		DoctorAvailabilityDto availability = doctorClient.findAvailabilityById(appointment.getAvailabilityId())
				.getBody().getData();
		availability.setIsAvailable(1);

		doctorClient.addDoctorAvailability(availability);
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// findAppointmentByPatientId
	public List<AppointmentDetailsDto> findByPatientId(String patientId) {

		List<Appointment> appointments = appointmentRepository.findAll();
		List<AppointmentDetailsDto> appointmentList = new ArrayList<AppointmentDetailsDto>();
		for (Appointment appointment : appointments) {

			if (appointment.getPatientId().equals(patientId)) {
				AppointmentDetailsDto appointmentDto = convertToAppointmentDetailsDto(appointment);
				appointmentList.add(appointmentDto);
				log.info("Successfully retrieved appointments with patientID: {}", patientId);

			}

		}

		return appointmentList;

	}

	// findAppointmentByDoctorId
	public List<AppointmentDetailsDto> findByDoctorId(String doctorId) {

		List<Appointment> appointments = appointmentRepository.findAll();
		List<AppointmentDetailsDto> appointmentList = new ArrayList<AppointmentDetailsDto>();
		for (Appointment appointment : appointments) {

			if (appointment.getDoctorId().equals(doctorId)) {
				AppointmentDetailsDto appointmentDto = convertToAppointmentDetailsDto(appointment);
				appointmentList.add(appointmentDto);
				log.info("Successfully retrieved appointments with doctorId: {}", doctorId);

			}
		}
		return appointmentList;

	}

	// ------------------------------------------------------------------------------------------------------------------------------

	// findAppointmentsWithinDateRange
	public List<AppointmentDetailsDto> findAppointmentsWithinDateRange(LocalDate startDate, LocalDate endDate) {
		List<Appointment> appointments = appointmentRepository.findAppointmentsInDateRange(startDate, endDate);

		List<AppointmentDetailsDto> appointmentsList = appointments.stream().map(this::convertToAppointmentDetailsDto)
				.collect(Collectors.toList());

		return appointmentsList;

	}

}