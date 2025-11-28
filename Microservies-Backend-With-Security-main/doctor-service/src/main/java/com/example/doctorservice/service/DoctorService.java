package com.example.doctorservice.service;

import com.example.doctorservice.client.AppointmentClient;
import com.example.doctorservice.client.MedicalHistoryClient;
import com.example.doctorservice.client.NotificationClient;
import com.example.doctorservice.dto.*;
import com.example.doctorservice.enums.Session;
//import com.example.doctorservice.enums.AppointmentStatus;
import com.example.doctorservice.exceptions.*;
import com.example.doctorservice.model.*;
import com.example.doctorservice.repository.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Slf4j
@Service
public class DoctorService {

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private DoctorAvailabilityRepository doctorAvailabilityRepository;

	@Autowired
	private AppointmentClient appointmentClient; // Inject Feign Client

	@Autowired
	private SpecializationRepository specializationRepository;

	@Autowired
	private MedicalHistoryClient medicalHistoryClient;

	@Autowired
	private NotificationClient notificationClient;

	// DTO Conversion Methods
	/**
	 * Converts a Doctor entity to a DoctorDto object.
	 *
	 * @param doctor the Doctor entity to convert.
	 * @return a DoctorDto object representing the given Doctor entity.
	 */
	// convertEntityToDTO
	private DoctorDto convertToDTO(Doctor doctor) {
		return new DoctorDto(doctor.getDoctorId(), doctor.getSpecialization().getSpecializationId(),doctor.getSpecialization().getSpecializationName(), doctor.getName(),
				doctor.getGender(),
				doctor.getContactDetails());
	}

	// convertDTOToEntity
	private Doctor convertToEntity(DoctorDto dDto) {
		Doctor d = new Doctor();

		d.setDoctorId(dDto.getDoctorId());
		d.setGender(dDto.getGender());
		d.setName(dDto.getName());
		d.setContactDetails(dDto.getContactDetails());

		return d;
	}

	// ---------------------------------------------------------------------------------------------------------------------

	// findAllDoctors
	public List<DoctorDto> findAll() {
		log.info("DoctorService::findAll:Entry");

		// Fetch all doctors from the repository
		List<Doctor> doctors = doctorRepository.findAll();

		// Convert doctors to DTOs
		List<DoctorDto> result = new ArrayList<>();
		for (Doctor d : doctors) {
			DoctorDto docDto = convertToDTO(d);
			result.add(docDto);
		}

		log.info("DoctorService::findAll:Exit successfully. Retrieved {} doctors.", result.size());
		return result;

	}

	/**
	 * Fetches details of a doctor by their unique ID.
	 *
	 * @param doctorId the unique identifier of the doctor.
	 * @return a DoctorDto object containing details of the doctor.
	 * @throws DoctorNotFoundException if no doctor is found with the given ID.
	 */
	// fetchDoctorById
	public DoctorDto fetchDoctorById(String doctorId) {
		log.info("Fetching doctor record with ID: {}", doctorId);

		// Fetch the doctor by ID
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorId));

		// Convert to DTO and return
		return convertToDTO(doctor);
	}

	// updateDoctor
	public DoctorDto updateDoctor(String doctorId, DoctorDto dDto) {
		log.info("DoctorService::updateDoctor:Entry for Doctor ID: {}", doctorId);

		Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);

		if (optionalDoctor.isPresent()) {
			Doctor existingDoctor = optionalDoctor.get();

			// Update doctor details
			existingDoctor.setName(dDto.getName());
			existingDoctor.setContactDetails(dDto.getContactDetails());
			existingDoctor.setGender(dDto.getGender());

			Specialization s = specializationRepository.findById(dDto.getSpecializationId()).orElseThrow(
					() -> new CustomException("Specialization not found with ID: " + dDto.getSpecializationId()));
			existingDoctor.setSpecialization(s);

			Doctor saved = doctorRepository.save(existingDoctor);

			log.info("DoctorService::updateDoctor:Doctor updated successfully for ID: {}", doctorId);
			return convertToDTO(saved);
		} else {
			log.warn("DoctorService::updateDoctor:Doctor not found for ID: {}", doctorId);
			throw new DoctorNotFoundException("Doctor not found with ID: " + doctorId);
		}

	}

	/**
	 * Deletes a doctor from the database by their unique ID.
	 *
	 * @param doctorId the unique identifier of the doctor to delete.
	 * @throws DoctorNotFoundException if no doctor is found with the given ID.
	 */
	// deleteDoctorById
	public void deleteById(String doctorId) {
		log.info("DoctorService::deleteById:Entry for Doctor ID: {}", doctorId);

		if (doctorRepository.existsById(doctorId)) {
			doctorRepository.deleteById(doctorId);
			log.info("DoctorService::deleteById:Doctor deleted successfully for ID: {}", doctorId);
		} else {
			log.warn("DoctorService::deleteById:Doctor not found for ID: {}", doctorId);
			throw new DoctorNotFoundException("Doctor not found with ID: " + doctorId);
		}

	}

	// deleteAllDoctors
	public void deleteAllDoctors() {
		log.info("DoctorService::deleteAllDoctors:Entry");

		doctorRepository.deleteAll();
		log.info("DoctorService::deleteAllDoctors:All doctors deleted successfully");

	}

	// -------------------------------------------------------------------------------------------------------------

	// findDoctorsBySpecializationName
	public List<DoctorDto> getDoctorsListBySpecializationName(String specializationName) {

		if (!(specializationRepository.existsBySpecializationName(specializationName))) {
			throw new CustomException("Specialization Name is not valid");
		}

		List<Doctor> doctors = doctorRepository.getDoctorsListBySpecializationName(specializationName);

//		if (doctors.isEmpty()) {
//			log.warn("No doctors found for specializationName: {}", specializationName);
//			throw new CustomException("No doctors found for specialization Name: " + specializationName);
//		}

		List<DoctorDto> result = new ArrayList<DoctorDto>();

		for (Doctor d : doctors) {
			DoctorDto docDto = convertToDTO(d);
			result.add(docDto);
		}
		return result;
	}

	/**
	 * Fetches all doctors associated with a specific specialization ID.
	 *
	 * @param specializationId the ID of the specialization to search for.
	 * @return a list of DoctorDto objects representing doctors with the given
	 *         specialization.
	 * @throws SpecializationNotFoundException if no doctors are found for the
	 *                                         specialization.
	 */
	// fetchDoctorsBySpecializationId
	public List<DoctorDto> fetchDoctorsBySpecializationId(String specializationId) {
		log.info("Fetching doctors with specializationId: {}", specializationId);

		if (!(specializationRepository.existsById(specializationId))) {
			throw new CustomException("Specialization not found with id : " + specializationId);
		}

		// Fetch doctors by specializationId
		List<Doctor> doctors = doctorRepository.findDoctorsBySpecialization(specializationId);

//		// Handle case where no doctors are found
//		if (doctors.isEmpty()) {
//			log.warn("No doctors found for specializationId: {}", specializationId);
//			throw new CustomException("No doctors found for specialization ID: " + specializationId);
//		}

		// Convert to a list of DoctorDto and return
		List<DoctorDto> doctorDtos = doctors.stream().map(this::convertToDTO).collect(Collectors.toList());

		log.info("Successfully fetched {} doctor(s) for specializationId: {}", doctorDtos.size(), specializationId);
		return doctorDtos;

	}

	// deleteDoctorById
	@Transactional
	public void deleteDoctorById(String doctorId) {
		log.info("Deleting doctor record with ID: {}", doctorId);

		// Check if doctor exists
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorId));

		// Delete the doctor
		doctorRepository.delete(doctor);

		log.info("Doctor record with ID: {} successfully deleted", doctorId);
	}

	// -----------------------------------------------------------------------------------------------------------------------------

	// getCurrentDoctorAppointments
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getCurrentDoctorAppointments(String doctorId) {
		if (!(doctorRepository.existsById(doctorId))) {
			throw new DoctorNotFoundException("Doctor not found with ID : " + doctorId);
		}

		ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> responseEntity = appointmentClient
				.getCurrentDoctorAppointments(doctorId);

//		if (responseEntity.getBody().getData().isEmpty()) {
//			throw new CustomException("No current appointments found for doctor with id : " + doctorId);
//		}
		return responseEntity;

	}

	// getPastDoctorAppointments
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> getPastDoctorAppointments(String doctorId) {

		if (!(doctorRepository.existsById(doctorId))) {
			throw new DoctorNotFoundException("Doctor not found with ID : " + doctorId);
		}

		ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> responseEntity = appointmentClient
				.getPastDoctorAppointments(doctorId);

//		if (responseEntity.getBody().getData().isEmpty()) {
//			throw new CustomException("No past appointments found for doctor with id : " + doctorId);
//		}
		return responseEntity;

	}

	// isDoctorAvailable
	public int isDoctorAvailable(String doctorId, LocalDate date, Session session) {
		log.info("Checking availability for doctor ID: {}, date: {}, session: {}", doctorId, date, session);

		Optional<DoctorAvailability> availability = doctorAvailabilityRepository
				.findDoctorByDoctorIdAndAvailableDateAndSessionAndIsAvailable(doctorId, date, session, 1);

		if (availability.isEmpty()) {
			log.warn("No available slot found for doctor ID: {}, date: {}, session: {}", doctorId, date, session);
			return 0;
		}

		log.info("Doctor is available for doctor ID: {}, date: {}, session: {}", doctorId, date, session);
		return 1;
	}

	// ------------------------------------------------------------------------------------------------------------------------------

	// create and save doctor
	@Transactional
	public UserRegisterDoctorDto addDoctor(UserRegisterDoctorDto uRDto) {
		log.info("uRDto in addDoctor : {}", uRDto);
		if (uRDto.getDoctorName() != null) {

			log.info("uRDto in addDoctor : {}", uRDto);
			Doctor d = new Doctor();

			d.setCreatedAt(uRDto.getCreatedAt());
			d.setUpdatedAt(uRDto.getUpdatedAt());
			d.setName(uRDto.getDoctorName());
			d.setDoctorId(uRDto.getDoctorId());
			d.setGender(uRDto.getGender());

			Specialization s = specializationRepository.findById(uRDto.getSpecializationId()).orElse(null);

			if (s == null) {
				throw new CustomException("Specialization ID is null. Specialization ID might not be valid");
			}

			d.setSpecialization(s);

			d.setContactDetails(uRDto.getContactDetails());

			Doctor savedDoctor = doctorRepository.save(d);

			uRDto.setDoctorId(savedDoctor.getDoctorId());
			uRDto.setUserId(savedDoctor.getDoctorId());
			uRDto.setSpecializationId(savedDoctor.getSpecialization().getSpecializationId());

		}
		return uRDto;
	}

	// ---------------------------------------------------------------------------------------------------------------------

	// completeAppointment
	public void completeAppointment(String appointmentId) {
		log.info("Completing appointment with ID: {}", appointmentId);

		appointmentClient.completeAppointment(appointmentId);

	}

	// addMedicalRecord
	public ResponseEntity<ResultResponse<MedicalHistoryDto>> addMedicalRecord(MedicalHistoryDto medicalHistoryDto) {

		AppointmentDetailsDto appointment = appointmentClient.findById(medicalHistoryDto.getAppointmentId()).getBody()
				.getData();
		String doctorId = appointment.getDoctorId();
		String patientId = appointment.getPatientId();

		medicalHistoryDto.setDoctorId(doctorId);
		medicalHistoryDto.setPatientId(patientId);
		medicalHistoryDto.setDateOfVisit(appointment.getAppointmentDate().toString());

		ResponseEntity<ResultResponse<MedicalHistoryDto>> result = medicalHistoryClient
				.addMedicalRecord(medicalHistoryDto);

		return result;
	}

	// markAppointmentAsCompleted
	@Transactional
	public ResponseEntity<ResultResponse<MedicalHistoryDto>> markAppointmentAsCompleted(
			MedicalHistoryDto medicalHistoryDto) {

		completeAppointment(medicalHistoryDto.getAppointmentId());
		ResponseEntity<ResultResponse<MedicalHistoryDto>> result = addMedicalRecord(medicalHistoryDto);

		return result;

	}

	// ---------------------------------------------------------------------------------------------------------------------------------

	// getPatientMedicalDetailsByPatientId
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalHistoryByPatientId(String patientId) {
		log.info("Fetching medical history by patient ID: {}", patientId);
		return medicalHistoryClient.getMedicalDetailsByPatientId(patientId);
	}

	// getDoctorMedicalDetailsByDoctorId
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalHistoryByDoctorId(String doctorId) {

		if (!(doctorRepository.existsById(doctorId))) {
			throw new DoctorNotFoundException("Doctor not found with ID : " + doctorId);
		}

		log.info("Fetching medical history by doctor ID: {}", doctorId);
		ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> medicalHistory = medicalHistoryClient
				.getMedicalDetailsByDoctorId(doctorId);
//		if (medicalHistory.getBody().getData().isEmpty()) {
//			throw new CustomException("No medical history details found for doctor with Id : " + doctorId);
//		}
		return medicalHistory;
	}

//	// getMedicalDetailsByPatientIdAndDate
//	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalHistoryPatientIdAndDate(String patientId,
//			String startDate, String endDate) {
//		log.info("Fetching medical history by patient ID and date: {}, {}, {}", patientId, startDate, endDate);
//		return medicalHistoryClient.getMedicalDetailsByPatientInfoAndDateVisit(patientId, startDate, endDate);
//	}

	// findDoctorAppointmentsByDoctorId
	public ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> findAppointmentByDoctorId(String doctorId) {

		if (!(doctorRepository.existsById(doctorId))) {
			throw new DoctorNotFoundException("Doctor not found with ID : " + doctorId);
		}

		ResponseEntity<ResultResponse<List<AppointmentDetailsDto>>> appointments = appointmentClient
				.findByDoctorId(doctorId);
//		if (appointments.getBody().getData().isEmpty()) {
//			throw new CustomException("Appointments not found for doctor with Id : " + doctorId);
//		}

		return appointments;
	}

	// getMedicalDetailsByDoctorIdAndPatientId
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> getMedicalDetialsByDoctorIdAndPatientId(
			String doctorId, String patientId) {
		return medicalHistoryClient.getMedicalDetialsByDoctorIdAndPatientId(doctorId, patientId);
	}

	// ----------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Cancel an appointment by its ID.
	 * 
	 * @param appointmentId the ID of the appointment to be canceled.
	 * @return the response message from Appointment Service.
	 */
	// cancelAppointmentByAppointmentId
	public ResponseEntity<ResultResponse<Void>> cancelAppointmentById(String appointmentId) {
		log.info("Initiating cancellation for appointmentId: {}", appointmentId);

		// Call Feign client to cancel appointment
		ResponseEntity<ResultResponse<Void>> response = appointmentClient.cancelAppointment(appointmentId);

		log.info("Appointment cancellation response: {}", response.getBody());
		return response;

	}

	// ----------------------------------------------------------------------------------------------------------------------------

	// getDoctorNotifications
	public ResponseEntity<ResultResponse<List<NotificationDto>>> getDoctorNotifications(String doctorId) {

		if (!(doctorRepository.existsById(doctorId))) {
			throw new DoctorNotFoundException("Doctor not found with ID : " + doctorId);
		}

		ResponseEntity<ResultResponse<List<NotificationDto>>> notifications = notificationClient
				.getDoctorNotificationsByDoctorId(doctorId);
//		if (notifications.getBody().getData().isEmpty()) {
//			throw new CustomException("No notifications found for doctor with Id : " + doctorId);
//		}
		return notifications;
	}

	// markDoctorNotificationAsRead
	public ResponseEntity<ResultResponse<Void>> markDoctorNotificationAsRead(String notificationId) {
		return notificationClient.markNotificationAsRead(notificationId);
	}

	// --------------------------------------------------------------------------------------------------------------------

	// filterPatientMedicalRecordsByDateRangeAndPatientId
	public ResponseEntity<ResultResponse<List<MedicalHistoryDto>>> filterPatientMedicalRecordsByDateRange(
			String startDate, String endDate, String patientId) {

		return medicalHistoryClient.filterMedicalDetailsByPatientIdAndDateRange(startDate, endDate, patientId);
	}

	// filterDoctorAppointmentsByDateRange
	public List<AppointmentDetailsDto> filterDoctorAppointmentByDateRange(String startDate, String endDate,
			String doctorId, String appointmentStatus) {
		List<AppointmentDetailsDto> appointments = appointmentClient.filterAppointmentsByDateRange(startDate, endDate)
				.getBody().getData();
		List<AppointmentDetailsDto> result = new ArrayList<AppointmentDetailsDto>();

		for (AppointmentDetailsDto appointment : appointments) {
			if (appointment.getDoctorId().equals(doctorId) && appointment.getStatus().equals(appointmentStatus)) {
				if(appointmentStatus.equals("SCHEDULED") && appointment.getAppointmentDate().isAfter(LocalDate.now())) {
					result.add(appointment);
				}
				result.add(appointment);
			}
		}

		return result;

	}

}
