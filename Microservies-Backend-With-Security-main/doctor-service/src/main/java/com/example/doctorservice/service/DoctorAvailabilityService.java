package com.example.doctorservice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.doctorservice.dto.DoctorAvailabilityDto;
import com.example.doctorservice.enums.Session;
import com.example.doctorservice.exceptions.CustomException;
import com.example.doctorservice.exceptions.DoctorAvailabilityNotFoundException;
import com.example.doctorservice.exceptions.DoctorNotFoundException;
import com.example.doctorservice.model.Doctor;
import com.example.doctorservice.model.DoctorAvailability;
import com.example.doctorservice.model.Specialization;
import com.example.doctorservice.repository.DoctorAvailabilityRepository;
import com.example.doctorservice.repository.DoctorRepository;
import com.example.doctorservice.repository.SpecializationRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoctorAvailabilityService {

	@Autowired
	private DoctorAvailabilityRepository daRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private SpecializationRepository specializationRepository;

	// convertEntityToDTO
	private DoctorAvailabilityDto convertToDTO(DoctorAvailability da) {
		DoctorAvailabilityDto daDto = new DoctorAvailabilityDto();
		daDto.setCreatedAt(LocalDateTime.now());
		daDto.setUpdatedAt(LocalDateTime.now());
		daDto.setAvailabilityId(da.getAvailabilityId());
		daDto.setDoctorId(da.getDoctor().getDoctorId());
		daDto.setSpecializationId(da.getSpecialization().getSpecializationId());
		daDto.setSession(da.getSession());
		daDto.setAvailableDate(da.getAvailableDate());
		daDto.setIsAvailable(da.getIsAvailable());
		// daDto.setDoctorAvailabilityId(da.getAvailabilityId());
		return daDto;
	}

	// convertDTOToEntity
	private DoctorAvailability convertToEntity(DoctorAvailabilityDto daDto) {
		DoctorAvailability da = new DoctorAvailability();

		da.setCreatedAt(daDto.getCreatedAt());
		da.setUpdatedAt(daDto.getUpdatedAt());
		da.setAvailabilityId(daDto.getAvailabilityId());

		Doctor d = doctorRepository.findById(daDto.getDoctorId()).orElse(null);
		da.setDoctor(d);

		Specialization s = specializationRepository.findById(daDto.getSpecializationId()).orElse(null);
		da.setSpecialization(s);

		da.setSession(daDto.getSession());
		da.setAvailableDate(daDto.getAvailableDate());
		da.setIsAvailable(daDto.getIsAvailable());

		return da;
	}

	// ----------------------------------------------------------------------------------------------------------------------

	// getAllDoctorAvailabilities
	public List<DoctorAvailabilityDto> findAll() {
		log.info("DoctorAvailabilityService::findAll:Entry");

		List<DoctorAvailability> dAvailabilities = daRepository.findAll();

		// Convert to DTOs
		List<DoctorAvailabilityDto> result = new ArrayList<>();
		for (DoctorAvailability da : dAvailabilities) {
			DoctorAvailabilityDto daDto = convertToDTO(da);
			result.add(daDto);
		}

		log.info("DoctorAvailabilityService::findAll:Successfully retrieved {} availability records.", result.size());
		return result;

	}

	// getDoctorAvailabilityById
	public DoctorAvailabilityDto findById(String availabilityId) {
		log.info("DoctorAvailabilityService::findById:Entry for Availability ID: {}", availabilityId);

		List<DoctorAvailability> dAvailabilities = daRepository.findAll();

		for (DoctorAvailability da : dAvailabilities) {
			if (da.getAvailabilityId().equals(availabilityId)) {
				DoctorAvailabilityDto daDto = convertToDTO(da);
				log.info("DoctorAvailabilityService::findById:Availability found for ID: {}", availabilityId);
				return daDto;
			}
		}

		log.warn("DoctorAvailabilityService::findById:No availability record found for ID: {}", availabilityId);
		throw new DoctorAvailabilityNotFoundException("DoctorAvailability not found for Id : " + availabilityId);

	}

	// addDoctorAvailability
	public DoctorAvailabilityDto addDoctorAvailability(DoctorAvailabilityDto daDto) {
		log.info("DoctorAvailabilityService::addDoctorAvailability:Entry with data: {}", daDto);

		if (!(doctorRepository.existsById(daDto.getDoctorId()))) {
			throw new DoctorNotFoundException("Doctor not found with id : " + daDto.getDoctorId());
		}

		if (!(specializationRepository.existsById(daDto.getSpecializationId()))) {
			throw new CustomException("Specialization not found with id : " + daDto.getSpecializationId());
		}

		if (daDto.getIsAvailable() != 1 && daDto.getIsAvailable() != 0) {
			throw new CustomException("Allowed isAvailable values are 0 | 1");
		}

		DoctorAvailability da = convertToEntity(daDto);
		DoctorAvailability saved = daRepository.save(da);

		log.info("DoctorAvailabilityService::addDoctorAvailability:Successfully added availability record with ID: {}",
				saved.getAvailabilityId());
		return convertToDTO(saved);

	}

	// updateDoctorAvailability
	public DoctorAvailabilityDto updateDoctorAvailability(String daId, DoctorAvailabilityDto daDto) {
		log.info("DoctorAvailabilityService::updateDoctorAvailability:Entry for Availability ID: {}", daId);

		if (!(doctorRepository.existsById(daDto.getDoctorId()))) {
			throw new DoctorNotFoundException("Doctor not found with id : " + daDto.getDoctorId());
		}

		if (!(specializationRepository.existsById(daDto.getSpecializationId()))) {
			throw new CustomException("Specialization not found with id : " + daDto.getSpecializationId());
		}

		if (daDto.getIsAvailable() != 1 && daDto.getIsAvailable() != 0) {
			throw new CustomException("Allowed isAvailable values are 0 | 1");
		}

		Optional<DoctorAvailability> optionalDa = daRepository.findById(daId);

		if (optionalDa.isPresent()) {
			DoctorAvailability existingDa = optionalDa.get();

			// Update availability details
			existingDa.setSession(daDto.getSession());
			existingDa.setAvailableDate(daDto.getAvailableDate());
			existingDa.setIsAvailable(daDto.getIsAvailable());

			DoctorAvailability saved = daRepository.save(existingDa);

			log.info(
					"DoctorAvailabilityService::updateDoctorAvailability:Successfully updated availability record for ID: {}",
					daId);
			return convertToDTO(saved);
		} else {
			log.warn("DoctorAvailabilityService::updateDoctorAvailability:Availability record not found for ID: {}",
					daId);
			throw new DoctorAvailabilityNotFoundException("Doctor Availability Record not found with ID: " + daId);
		}

	}

	// deleteDoctorAvailabilityById
	public void deleteById(String daId) {
		log.info("DoctorAvailabilityService::deleteById:Entry for Availability ID: {}", daId);

		if (daRepository.existsById(daId)) {
			daRepository.deleteById(daId);
			log.info("DoctorAvailabilityService::deleteById:Successfully deleted availability record for ID: {}", daId);
		} else {
			log.warn("DoctorAvailabilityService::deleteById:Availability record not found for ID: {}", daId);
			throw new DoctorAvailabilityNotFoundException("Doctor Availability Record not found with ID: " + daId);
		}

	}

	// deleteAllDoctorAvailabilityRecords
	public void deleteAll() {
		log.info("DoctorAvailabilityService::deleteAll:Entry");

		daRepository.deleteAll();
		log.info("DoctorAvailabilityService::deleteAll:Successfully deleted all availability records");

	}

	// --------------------------------------------------------------------------------------------------------------------------------

	// getDoctorSchedule
	public List<DoctorAvailabilityDto> getDoctorSchedule(String doctorId) {

		if (!(doctorRepository.existsById(doctorId))) {
			throw new DoctorNotFoundException("Doctor not found with id : " + doctorId);
		}

		List<DoctorAvailability> doctorAvailability = daRepository.getDoctorSchedule(doctorId);
		List<DoctorAvailabilityDto> result = new ArrayList<DoctorAvailabilityDto>();

		for (DoctorAvailability da : doctorAvailability) {

			DoctorAvailabilityDto daDto = convertToDTO(da);
			result.add(daDto);
		}

		return result;

	}

	// findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailable
	public Optional<DoctorAvailabilityDto> findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailable(
			String doctorId, LocalDate appointmentDate, Session session, int isAvailable) {

		if (!(doctorRepository.existsById(doctorId))) {
			throw new DoctorNotFoundException("Doctor not found with id : " + doctorId);
		}

		if (isAvailable != 1 && isAvailable != 0) {
			throw new CustomException("Allowed isAvailable values are 0 | 1");
		}

		if (!(daRepository.existsByAvailableDate(appointmentDate))) {
			throw new CustomException("DoctorAvailability does not exist for the given appointmentDate");
		}

		Optional<DoctorAvailability> optionalSlot = daRepository
				.findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailable(doctorId, appointmentDate,
						session, isAvailable);

		if (optionalSlot.isEmpty()) {
			throw new DoctorAvailabilityNotFoundException(
					"Doctor availability record not found for the given input parameters");
		}

		// Use map to convert the optionalSlot to an Optional<DoctorAvailabilityDto>
		return optionalSlot.map(this::convertToDTO);
	}

	/**
	 * Fetches available doctors based on specialization name, available date, and
	 * session.
	 *
	 * @param specializationName The name of the doctor's specialization.
	 * @param availableDate      The date on which the doctor is available.
	 * @param session            The session (FN or AN) when the doctor is
	 *                           available.
	 * @return A list of DoctorAvailabilityDto containing available doctor details.
	 */
	// getAvailableDoctorsForBookingAndReschedulingAppointments
	public List<DoctorAvailabilityDto> getAvailableDoctors(String specializationName, LocalDate availableDate,
			Session session) {

		if (!(specializationRepository.existsBySpecializationName(specializationName))) {
			throw new CustomException("Specialization not found with the name : " + specializationName);
		}

		if (!(daRepository.existsByAvailableDate(availableDate))) {
			throw new DoctorAvailabilityNotFoundException(
					"Availability records not found for available date : " + availableDate);
		}

		List<DoctorAvailability> entities = daRepository
				.findBySpecialization_SpecializationNameAndAvailableDateAndSessionAndIsAvailable(specializationName,
						availableDate, session, 1);

		if (entities.isEmpty()) {
			throw new CustomException("No doctors available for the available date and session and specialization");
		}

		// Map entities to DTOs
		return entities.stream()
				.map(entity -> DoctorAvailabilityDto.builder().availabilityId(entity.getAvailabilityId())
						.createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt())
						.doctorId(entity.getDoctor().getDoctorId())
						.specializationId(entity.getSpecialization().getSpecializationId())
						.availableDate(entity.getAvailableDate()).session(entity.getSession())
						.isAvailable(entity.getIsAvailable()).build())
				.collect(Collectors.toList());
	}

	// --------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Updates the availability status of a specific slot by toggling its value (1 ↔
	 * 0).
	 *
	 * @param availabilityId the ID of the availability slot to update.
	 * @throws DoctorAvailabilityNotFoundException if no record is found for the
	 *                                             given ID.
	 */
	// updateAvailabilitySlotStatus
	@Transactional
	public void updateAvailabilitySlotStatus(String availabilityId) {
		log.info("Toggling availability status for slot with ID: {}", availabilityId);

		// Fetch the availability record
		DoctorAvailability availability = daRepository.findById(availabilityId).orElseThrow(
				() -> new DoctorAvailabilityNotFoundException("Availability not found with ID: " + availabilityId));

		// Toggle the availability status (2 ↔ 1)
		int currentStatus = availability.getIsAvailable();
		availability.setIsAvailable(currentStatus == 2 ? 1 : 2);

		// Save the updated availability back to the database
		daRepository.save(availability);
		log.info("Availability status successfully toggled for slot with ID: {}", availabilityId);
	}

	// ----------------------------------------------------------------------------------------------------------------------

	// getDoctorAvailabilityByDateRangeAndDoctorId
	public List<DoctorAvailabilityDto> getDoctorAvailabilityByDateRange(LocalDate startDate, LocalDate endDate,
			String doctorId) {
		List<DoctorAvailability> schedule = daRepository.findAvailabilityInDateRange(startDate, endDate);
		List<DoctorAvailabilityDto> result = new ArrayList<DoctorAvailabilityDto>();

		for (DoctorAvailability slot : schedule) {
			if (slot.getDoctor().getDoctorId().equals(doctorId)) {
				DoctorAvailabilityDto convertedSlot = convertToDTO(slot);
				result.add(convertedSlot);
			}
		}

		return result;
	}

}
