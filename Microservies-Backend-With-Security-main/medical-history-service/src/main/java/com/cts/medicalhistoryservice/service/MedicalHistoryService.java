package com.cts.medicalhistoryservice.service;

import com.cts.medicalhistoryservice.client.AppointmentClient;
import com.cts.medicalhistoryservice.client.DoctorClient;
import com.cts.medicalhistoryservice.client.PatientClient;
import com.cts.medicalhistoryservice.dto.AppointmentDetailsDto;
import com.cts.medicalhistoryservice.dto.DoctorDto;
import com.cts.medicalhistoryservice.dto.MedicalHistoryDto;
import com.cts.medicalhistoryservice.dto.PatientDto;
import com.cts.medicalhistoryservice.dto.SpecializationDto;
import com.cts.medicalhistoryservice.entity.MedicalHistory;

import com.cts.medicalhistoryservice.exception.DoctorIdNotFoundException;
import com.cts.medicalhistoryservice.exception.MedicalRecordCreationException;
import com.cts.medicalhistoryservice.exception.MedicalRecordNotFoundException;
import com.cts.medicalhistoryservice.exception.PatientIdNotFoundException;
import com.cts.medicalhistoryservice.repository.MedicalHistoryRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MedicalHistoryService {

	@Autowired
	private MedicalHistoryRepository medicalHistoryRepository;

	@Autowired
	private AppointmentClient appointmentClient;

	@Autowired
	private PatientClient patientClient;
	
	@Autowired 
	private DoctorClient doctorClient;

	/**
	 * Converts a MedicalHistoryEntity to a MedicalHistoryDto.
	 *
	 * @param medicalHistory The entity to be converted.
	 * @return The converted DTO.
	 */
	// convertEntityToDto
	private MedicalHistoryDto convertToDto(MedicalHistory medicalHistory) {
		MedicalHistoryDto medicalHistoryDto = new MedicalHistoryDto();
		medicalHistoryDto.setHistoryId(medicalHistory.getHistoryId());
		medicalHistoryDto.setAppointmentId(medicalHistory.getAppointmentId());
		medicalHistoryDto.setDoctorId(medicalHistory.getDoctorId());
		medicalHistoryDto.setPatientId(medicalHistory.getPatientId());
		medicalHistoryDto.setMedications(medicalHistory.getMedications());
		medicalHistoryDto.setDateOfVisit(medicalHistory.getDateOfVisit().toString());
		// medicalHistoryDto.setName(medicalHistory.getName());
		medicalHistoryDto.setDiagnosis(medicalHistory.getDiagnosis());
		medicalHistoryDto.setTreatment(medicalHistory.getTreatment());
		medicalHistoryDto.setCreatedAt(medicalHistory.getCreatedAt());
		medicalHistoryDto.setUpdatedAt(medicalHistory.getUpdatedAt());
		medicalHistoryDto.setBloodPressure(medicalHistory.getBloodPressure());
		medicalHistoryDto.setTemperature(medicalHistory.getTemperature());
		medicalHistoryDto.setHeartRate(medicalHistory.getHeartRate());

		PatientDto patient = patientClient.fetchPatientById(medicalHistory.getPatientId()).getBody().getData();
		String patientName = patient.getName();
		
		DoctorDto doctor = doctorClient.fetchDoctorById(medicalHistory.getDoctorId()).getBody().getData();
		String doctorName = doctor.getName();
		
		SpecializationDto specialization = doctorClient.findById(doctor.getSpecializationId()).getBody().getData();
		String specializationName = specialization.getSpecializationName();

		medicalHistoryDto.setPatientName(patientName);
		medicalHistoryDto.setDoctorName(doctorName);
		medicalHistoryDto.setSpecializationName(specializationName);

		return medicalHistoryDto;
	}

	/**
	 * Converts a MedicalHistoryDto to a MedicalHistoryEntity.
	 *
	 * @param medicalHistoryDto The DTO to be converted.
	 * @return The converted entity.
	 */
	// convertDtoToEntity
	public MedicalHistory convertToEntity(MedicalHistoryDto medicalHistoryDto) {
		MedicalHistory medicalHistory = new MedicalHistory();
		medicalHistory.setPatientId(medicalHistoryDto.getPatientId());
		medicalHistory.setDoctorId(medicalHistoryDto.getDoctorId());
		medicalHistory.setAppointmentId(medicalHistoryDto.getAppointmentId());
		medicalHistory.setMedications(medicalHistoryDto.getMedications());
		medicalHistory.setDiagnosis(medicalHistoryDto.getDiagnosis());
		medicalHistory.setTreatment(medicalHistoryDto.getTreatment());
		medicalHistory.setBloodPressure(medicalHistoryDto.getBloodPressure());
		medicalHistory.setHeartRate(medicalHistoryDto.getHeartRate());
		medicalHistory.setTemperature(medicalHistoryDto.getTemperature());

		// Convert dateOfVisit from String to LocalDate
		if (medicalHistoryDto.getDateOfVisit() != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate dov = LocalDate.parse(medicalHistoryDto.getDateOfVisit(), formatter);
			medicalHistory.setDateOfVisit(dov);
		}

		return medicalHistory;
	}

	// -----------------------------------------------------------------------------------------------------------------------

	// findAllMedicalHistories
	public List<MedicalHistoryDto> findAll() {

		List<MedicalHistory> mHistories = medicalHistoryRepository.findAll();

		if (mHistories.isEmpty()) {
			throw new MedicalRecordNotFoundException("No medical records found");
		}

		List<MedicalHistoryDto> result = new ArrayList<MedicalHistoryDto>();

		for (MedicalHistory mh : mHistories) {
			MedicalHistoryDto mhDto = convertToDto(mh);
			result.add(mhDto);
		}
		return result;

	}

	// findMedicalHistoryById
	public MedicalHistoryDto findById(String medHistoryId) {

		List<MedicalHistory> medHistories = medicalHistoryRepository.findAll();

		for (MedicalHistory mh : medHistories) {
			if (mh.getHistoryId().equals(medHistoryId)) {
				MedicalHistoryDto mhDto = convertToDto(mh);
				return mhDto;
			}
		}
		throw new MedicalRecordNotFoundException("Medical Record not found for id : " + medHistoryId);
	}

	// addMedicalHistory
	public MedicalHistoryDto addMedicalHistory(MedicalHistoryDto mhDto) {

		MedicalHistory mh = convertToEntity(mhDto);
		MedicalHistory saved = medicalHistoryRepository.save(mh);
		return convertToDto(saved);
	}

	// updateMedicalHistory
	public MedicalHistoryDto updateMedicalHistory(String mhId, MedicalHistoryDto mhDto) {
		Optional<MedicalHistory> optionalMH = medicalHistoryRepository.findById(mhId);

		if (optionalMH.isPresent()) {
			MedicalHistory existingMH = optionalMH.get();

			existingMH.setDiagnosis(mhDto.getDiagnosis());
			existingMH.setTreatment(mhDto.getTreatment());
			existingMH.setMedications(mhDto.getMedications());

			MedicalHistory saved = medicalHistoryRepository.save(existingMH);
			return convertToDto(saved);
		} else {
			throw new MedicalRecordNotFoundException("Medical History not found with id : " + mhId);
		}
	}

	// deleteMedicalHistoryById
	public void deleteById(String mhId) {
		if (medicalHistoryRepository.existsById(mhId)) {
			medicalHistoryRepository.deleteById(mhId);
		} else {
			throw new MedicalRecordNotFoundException("Medical History not found with id : " + mhId);
		}
	}

	// deleteAllMedicalHistory
	public void deleteAllMedicalHistory() {
		medicalHistoryRepository.deleteAll();
	}

	// --------------------------------------------------------------------------------------------------------------------------

	/**
	 * Retrieves the medical details of a patient by their ID.
	 *
	 * @param patientId The ID of the patient.
	 * @return A list of MedicalHistoryDto objects.
	 */
	// getPatientMedicalDetailsByPatientId
	public List<MedicalHistoryDto> getMedicalDetailsByPatientId(String patientId) {
		log.info("MedicalHistoryService::getMedicalDetailsByPatientId:Entry");

		List<MedicalHistory> medicalHistories = medicalHistoryRepository.findByPatientId(patientId);

//		if (medicalHistories == null || medicalHistories.isEmpty()) {
//			throw new PatientIdNotFoundException("Medical details not found for patient with id : " + patientId
//					+ ". Either the patient doesn't exist with the given id or the patient exists and doesn't have any medical records yet");
//		}

		List<MedicalHistoryDto> result = medicalHistories.stream().map(this::convertToDto).collect(Collectors.toList());

		log.info("MedicalHistoryService::getMedicalDetailsByPatientId:Exit");

		return result;
	}

	/**
	 * Retrieves the medical details of patients by the doctor's ID.
	 *
	 * @param doctorId The ID of the doctor.
	 * @return A list of MedicalHistoryDto objects.
	 */
	// getDoctorMedicalDetailsByDoctorId
	public List<MedicalHistoryDto> getMedicalDetailsByDoctorId(String doctorId) {
		log.info("MedicalHistoryService::getMedicalDetailsByDoctorId:Entry");

		List<MedicalHistory> medicalHistories = medicalHistoryRepository.findByDoctorId(doctorId);

//		if (medicalHistories == null || medicalHistories.isEmpty()) {
//			throw new DoctorIdNotFoundException("Medical history details not found for doctor with id : " + doctorId
//					+ ". Either the doctor doesn't exist with the given id or the doctor exists and didn't prescribe any medical records yet");
//		}

		List<MedicalHistoryDto> result = medicalHistories.stream().map(this::convertToDto).collect(Collectors.toList());

		log.info("MedicalHistoryService::getMedicalDetailsByDoctorId:Exit");

		return result;
	}

	/**
	 * Retrieves the medical details of a patient by their ID and date of visit
	 * range.
	 *
	 * @param patientId The ID of the patient.
	 * @param startDate The start date of the visit range.
	 * @param endDate   The end date of the visit range.
	 * @return A list of MedicalHistoryDto objects.
	 */
//	// getPatientMedicalDetailsWithinDateRange ---> not working as intended
//	public List<MedicalHistoryDto> getMedicalDetailsByPatientIdAndDateOfVisit(String patientId, LocalDate startDate,
//			LocalDate endDate) {
//		log.info("MedicalHistoryService::getMedicalDetailsByPatientIdAndDateOfVisit:Entry");
//
//		List<MedicalHistory> medicalHistories = medicalHistoryRepository
//				.getMedicalDetailsByPatientIdAndDateVisit(patientId, startDate, endDate);
//
//		if (medicalHistories == null || medicalHistories.isEmpty()) {
//			throw new MedicalRecordNotFoundException(
//					"Medical records not found for patient id: " + patientId + " and given dates");
//		}
//
//		List<MedicalHistoryDto> result = medicalHistories.stream().map(this::convertToDto).collect(Collectors.toList());
//
//		log.info("MedicalHistoryService::getMedicalDetailsByPatientIdAndDateOfVisit:Exit");
//
//		return result;
//	}

	/**
	 * Adds a new medical record to the database.
	 *
	 * @param medicalHistoryDto The DTO containing the details of the medical record
	 *                          to be added.
	 * @return The added MedicalHistoryDto.
	 */
	// addMedicalRecords
	public MedicalHistoryDto addMedicalRecords(MedicalHistoryDto medicalHistoryDto) {
		log.info("MedicalHistoryService::addMedicalRecords:Entry");
		log.info("------------------MedicalHistoryDto : {}---------------", medicalHistoryDto);

		AppointmentDetailsDto appointment = appointmentClient.findById(medicalHistoryDto.getAppointmentId()).getBody()
				.getData();
		LocalDate dateOfVisit = appointment.getAppointmentDate();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Desired format
		String formattedDate = dateOfVisit.format(formatter);

		medicalHistoryDto.setDateOfVisit(formattedDate);
		medicalHistoryDto.setDoctorId(appointment.getDoctorId());
		medicalHistoryDto.setPatientId(appointment.getPatientId());

		// log.info

		try {
			MedicalHistory medicalHistory = convertToEntity(medicalHistoryDto);
			MedicalHistory savedMedicalHistory = medicalHistoryRepository.save(medicalHistory);

			MedicalHistoryDto result = convertToDto(savedMedicalHistory);

			log.info("MedicalHistoryService::addMedicalRecords:Exit");

			return result;
		} catch (Exception e) {
			log.error("MedicalHistoryService::addMedicalRecords:Error creating medical record", e);
			throw new MedicalRecordCreationException("Error creating medical record: " + e.getMessage());
		}
	}

	// getMedicalDetailsByDoctorIdAndPatientId
	public List<MedicalHistoryDto> getMedicalDetialsByDoctorIdAndPatientId(String doctorId, String patientId) {
		List<MedicalHistory> medicalHistories = medicalHistoryRepository.findAll();
		List<MedicalHistoryDto> result = new ArrayList<MedicalHistoryDto>();

		for (MedicalHistory history : medicalHistories) {
			if ((history.getDoctorId().equals(doctorId)) && (history.getPatientId().equals(patientId))) {
				result.add(convertToDto(history));
			}
		}
//		if (result == null || result.isEmpty()) {
//			throw new MedicalRecordNotFoundException("Medical records not found between patient with id : " + patientId
//					+ " and doctor with id : " + doctorId);
//		}

		return result;

	}

	// getMedicalDetailsWithinDateRange
	public List<MedicalHistoryDto> getMedicalDetailsWithinDateRange(LocalDate startDate, LocalDate endDate,
			String patientId) {
		List<MedicalHistory> medicalDetails = medicalHistoryRepository.findMedicalHistoryInDateRange(startDate,
				endDate);
		List<MedicalHistoryDto> result = new ArrayList<MedicalHistoryDto>();

		for (MedicalHistory record : medicalDetails) {
			if (record.getPatientId().equals(patientId)) {
				result.add(convertToDto(record));
			}
		}

		return result;

	}
}