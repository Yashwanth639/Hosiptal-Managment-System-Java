package com.example.loginservice.service;

import com.example.loginservice.client.AppointmentClient;
import com.example.loginservice.client.DoctorClient;
import com.example.loginservice.client.MedicalHistoryClient;
import com.example.loginservice.client.NotificationClient;
import com.example.loginservice.dto.AppointmentDetailsDto;
import com.example.loginservice.dto.BookAppointmentDto;
import com.example.loginservice.dto.DoctorAvailabilityDto;
import com.example.loginservice.dto.MedicalHistoryDto;
import com.example.loginservice.dto.NotificationDto;
import com.example.loginservice.dto.PatientDto;
import com.example.loginservice.dto.RescheduleRequestDto;
import com.example.loginservice.enums.AppointmentStatus;
import com.example.loginservice.model.Patient;
import com.example.loginservice.model.ResultResponse;

import com.example.loginservice.exception.ResourceNotFoundException;

import com.example.loginservice.repository.PatientRepository;
import com.example.loginservice.repository.RoleRepository;
import com.example.loginservice.repository.UserRepository;
//import com.example.loginservice.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

@Service
@Slf4j
public class PatientService {

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private MedicalHistoryClient medicalHistoryClient;

	@Autowired
	private DoctorClient doctorClient;

	@Autowired
	private NotificationClient notificationClient;

	@Autowired
	private AppointmentClient appointmentClient;

	// convertEntityToDTO
	private PatientDto convertToDTO(Patient p) {
		PatientDto pDto = new PatientDto();
		pDto.setCreatedAt(p.getCreatedAt());
		pDto.setUpdatedAt(p.getUpdatedAt());
		pDto.setPatientId(p.getPatientId());
		pDto.setName(p.getName());
		pDto.setDateOfBirth(p.getDateOfBirth());
		pDto.setContactDetails(p.getContactDetails());
		pDto.setGender(p.getGender());
		pDto.setWeightInKg(p.getWeightInCm());
		pDto.setHeightInCm(p.getHeightInCm());

		return pDto;
	}

	// convertDTOToEntity
	private Patient convertToEntity(PatientDto pDto) {
		Patient p = new Patient();
		p.setCreatedAt(pDto.getCreatedAt());
		p.setUpdatedAt(pDto.getUpdatedAt());
		p.setPatientId(pDto.getPatientId());

//  		User user = userRepository.findById(pDto.getUserId()).orElse(null);
//  		p.setUser(user);

		p.setName(pDto.getName());
		p.setDateOfBirth(pDto.getDateOfBirth());
		p.setContactDetails(pDto.getContactDetails());
		p.setGender(pDto.getGender());
		p.setHeightInCm(pDto.getHeightInCm());
		p.setWeightInCm(pDto.getWeightInKg());

		return p;
	}

	// findAllPatients
	public List<PatientDto> findAll() {
		log.info("PatientService::findAll:Entry");

		List<Patient> patients = patientRepository.findAll();

		// Convert patients to DTOs
		List<PatientDto> result = new ArrayList<>();
		for (Patient p : patients) {
			PatientDto pDto = convertToDTO(p);
			result.add(pDto);
		}

		log.info("PatientService::findAll:Exit successfully");
		return result;

	}

	// findPatientById
	public PatientDto fetchPatientById(String patientId) {
		log.info("PatientService::fetchPatientById:Entry for Patient ID: {}", patientId);

		Patient patient = patientRepository.findById(patientId)
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

		log.info("PatientService::fetchPatientById:Patient found with ID: {}", patientId);
		return convertToDTO(patient);

	}

	// findPatientByName
	public PatientDto findByName(String pName) {
		log.info("PatientService::findByName:Entry for Patient Name: {}", pName);

		List<Patient> patients = patientRepository.findAll();

		for (Patient p : patients) {
			if (pName.chars().allMatch(ch -> p.getName().indexOf(ch) >= 0)) {
				log.info("PatientService::findByName:Patient found with Name: {}", pName);
				return convertToDTO(p);
			}
		}

		log.warn("PatientService::findByName:Patient not found with Name: {}", pName);
		throw new ResourceNotFoundException("Patient not found with Name: " + pName);

	}

	// updatePatientDetails
	public PatientDto updatePatient(String patientId, PatientDto pDto) {
		log.info("PatientService::updatePatient:Entry for Patient ID: {}", patientId);

		Optional<Patient> optionalPatient = patientRepository.findById(patientId);

		if (optionalPatient.isPresent()) {
			Patient existingPatient = optionalPatient.get();

			// Update the patient details
			existingPatient.setName(pDto.getName());
			existingPatient.setDateOfBirth(pDto.getDateOfBirth());
			existingPatient.setContactDetails(pDto.getContactDetails());
			existingPatient.setGender(pDto.getGender());
			existingPatient.setHeightInCm(pDto.getHeightInCm());
			existingPatient.setWeightInCm(pDto.getWeightInKg());

			Patient saved = patientRepository.save(existingPatient);

			log.info("PatientService::updatePatient:Patient updated successfully for ID: {}", patientId);
			return convertToDTO(saved);

		} else {
			log.warn("PatientService::updatePatient:Patient not found with ID: {}", patientId);
			throw new ResourceNotFoundException("Patient not found with ID: " + patientId);
		}

	}

	// deletePatientById
	public void deleteById(String patientId) {
		log.info("PatientService::deleteById:Entry for Patient ID: {}", patientId);

		if (patientRepository.existsById(patientId)) {
			patientRepository.deleteById(patientId);
			log.info("PatientService::deleteById:Patient deleted successfully for ID: {}", patientId);
		} else {
			log.warn("PatientService::deleteById:Patient not found with ID: {}", patientId);
			throw new ResourceNotFoundException("Patient not found with ID: " + patientId);
		}

	}

	// deleteAllPatients
	public void deleteAllPatients() {
		log.info("PatientService::deleteAllPatients:Entry");

		patientRepository.deleteAll();
		log.info("PatientService::deleteAllPatients:All patients deleted successfully");

	}

	// ----------------------------------------------------------------------------------------------------------------------------------------------------

	// getCurrentPatientAppointments
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getCurrentPatientAppointments(
			@PathVariable String patientId) {

		if (!(patientRepository.existsById(patientId))) {
			throw new ResourceNotFoundException("Patient not found with ID : " + patientId);
		}
		return appointmentClient.getCurrentPatientAppointments(patientId);
	}

	// getPastPatientAppointments
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getPastPatientAppointments(
			@PathVariable String patientId) {
		if (!(patientRepository.existsById(patientId))) {
			throw new ResourceNotFoundException("Patient not found with ID : " + patientId);
		}
		return appointmentClient.getPastPatientAppointments(patientId);
	}

	// getPatientMedicalHistoriesByPatientId
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetailsByPatientId(
			@PathVariable String patientId) {
		if (!(patientRepository.existsById(patientId))) {
			throw new ResourceNotFoundException("Patient not found with ID : " + patientId);
		}
		return medicalHistoryClient.getMedicalDetailsByPatientId(patientId);
	}

	// getAppointmentsByPatientId
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> findAppointmentByPatientId(String patientId) {
		if (!(patientRepository.existsById(patientId))) {
			throw new ResourceNotFoundException("Patient not found with ID : " + patientId);
		}
		return appointmentClient.findByPatientId(patientId);
	}

	// getAvailableDoctorsForBookingAndReschedulingAppointment
	public ResponseEntity<List<DoctorAvailabilityDto>> getAvailableDoctors(String specializationName,
			LocalDate availableDate, String session) {
		// Format the date to 'yyyy-MM-dd'
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedDate = availableDate.format(formatter);

		return doctorClient.getAvailableDoctors(specializationName, formattedDate, session);
	}

	// ----------------------------------------------------------------------------------------------------------------------------------------

	// bookAppointment
	public ResponseEntity<ResultResponse<BookAppointmentDto>> bookAppointment(BookAppointmentDto rDto) {
		ResponseEntity<ResultResponse<BookAppointmentDto>> appointment = appointmentClient.bookAppointment(rDto);
		return appointment;
	}

	// rescheduleAppointment
	public ResponseEntity<ResultResponse<RescheduleRequestDto>> rescheduleAppointment(RescheduleRequestDto rDto) {

		ResponseEntity<ResultResponse<RescheduleRequestDto>> appointment = appointmentClient
				.rescheduleAppointment(rDto);
		return appointment;
	}

	// cancelAppointment
	public ResponseEntity<ResultResponse<Void>> cancelAppointment(String appointmentId) {
		ResponseEntity<ResultResponse<Void>> v = appointmentClient.cancelAppointment(appointmentId);
		return v;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------

	// displayPatientNotifications
	public ResponseEntity<ResultResponse<List<NotificationDto>>> getPatientNotifications(String patientId) {
		if (!(patientRepository.existsById(patientId))) {
			throw new ResourceNotFoundException("Patient not found with ID : " + patientId);
		}
		return notificationClient.getPatientNotificationsByPatientId(patientId);
	}

	// markPatientNotificationAsRead
	public ResponseEntity<ResultResponse<Void>> markPatientNotificationAsRead(String notificationId) {
		return notificationClient.markNotificationAsRead(notificationId);
	}

	// -----------------------------------------------------------------------------------------------------------------

	// filterPatientAppointmentsByDateRange
	public List<AppointmentDetailsDto> filterPatientAppointmentByDateRange(String startDate, String endDate,
			String patientId, String appointmentStatus) {
		List<AppointmentDetailsDto> appointments = appointmentClient.filterAppointmentsByDateRange(startDate, endDate)
				.getBody().getData();
		List<AppointmentDetailsDto> result = new ArrayList<AppointmentDetailsDto>();

		AppointmentStatus status = AppointmentStatus.valueOf(appointmentStatus);

		for (AppointmentDetailsDto appointment : appointments) {
			if ((appointment.getPatientId().equals(patientId)) && (appointment.getStatus().equals(status))) {
				result.add(appointment);
			}
		}

		return result;

	}
}