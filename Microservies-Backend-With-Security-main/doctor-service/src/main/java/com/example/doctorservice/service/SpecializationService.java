package com.example.doctorservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.doctorservice.dto.SpecializationDto;
import com.example.doctorservice.exceptions.CustomException;

import com.example.doctorservice.model.Specialization;
import com.example.doctorservice.repository.SpecializationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SpecializationService {

	@Autowired
	private SpecializationRepository specializationRepo;

	// convertEntityToDTO
	private SpecializationDto convertToDTO(Specialization s) {
		SpecializationDto sDto = new SpecializationDto();
		sDto.setCreatedAt(s.getCreatedAt());
		sDto.setUpdatedAt(s.getUpdatedAt());
		sDto.setSpecializationId(s.getSpecializationId());
		sDto.setSpecializationName(s.getSpecializationName());

		return sDto;
	}

	// convertDTOToEntity
	private Specialization convertToEntity(SpecializationDto sDto) {
		Specialization s = new Specialization();
		s.setCreatedAt(sDto.getCreatedAt());
		s.setUpdatedAt(sDto.getUpdatedAt());
		s.setSpecializationId(sDto.getSpecializationId());
		s.setSpecializationName(sDto.getSpecializationName());

		return s;
	}

	// findAllSpecializations
	public List<SpecializationDto> findAll() {
		log.info("SpecializationService::findAll:Entry");

		List<Specialization> specs = specializationRepo.findAll();

		// Convert specializations to DTOs
		List<SpecializationDto> result = new ArrayList<>();
		for (Specialization s : specs) {
			SpecializationDto sDto = convertToDTO(s);
			result.add(sDto);
		}

		log.info("SpecializationService::findAll:Exit successfully. Retrieved {} specializations.", result.size());
		return result;

	}

	// findSpecializationById
	public SpecializationDto findById(String specId) {
		log.info("SpecializationService::findById:Entry for Specialization ID: {}", specId);

		List<Specialization> specs = specializationRepo.findAll();

		for (Specialization s : specs) {
			if (s.getSpecializationId().equals(specId)) {
				SpecializationDto sDto = convertToDTO(s);
				log.info("SpecializationService::findById:Specialization found for ID: {}", specId);
				return sDto;
			}
		}

		log.warn("SpecializationService::findById:No specialization found for ID: {}", specId);
		throw new CustomException("No specialization found for ID: " + specId);

	}

	// findSpecializationByName
	public SpecializationDto findByName(String name) {
		log.info("SpecializationService::findByName:Entry for Specialization Name: {}", name);

		List<Specialization> specs = specializationRepo.findAll();

		for (Specialization s : specs) {
			if (s.getSpecializationName().equals(name)) {
				SpecializationDto sDto = convertToDTO(s);
				log.info("SpecializationService::findByName:Specialization found for Name: {}", name);
				return sDto;
			}
		}

		log.warn("SpecializationService::findByName:No specialization found for Name: {}", name);
		throw new CustomException("No specialization found for Name: " + name);

	}

	// saveSpecialization
	public SpecializationDto saveSpecialization(SpecializationDto sDto) {
		log.info("SpecializationService::saveSpecialization:Entry with data: {}", sDto);

		Specialization s = convertToEntity(sDto);
		Specialization saved = specializationRepo.save(s);
		log.info("SpecializationService::saveSpecialization:Specialization saved successfully with ID: {}",
				saved.getSpecializationId());
		return convertToDTO(saved);

	}

	// updateSpecialization
	public SpecializationDto updateSpecialization(String specId, SpecializationDto sDto) {
		log.info("SpecializationService::updateSpecialization:Entry for Specialization ID: {}", specId);

		Optional<Specialization> optSpecialization = specializationRepo.findById(specId);

		if (optSpecialization.isPresent()) {
			Specialization existingSpec = optSpecialization.get();
			existingSpec.setSpecializationName(sDto.getSpecializationName());
			Specialization saved = specializationRepo.save(existingSpec);

			log.info("SpecializationService::updateSpecialization:Specialization updated successfully for ID: {}",
					specId);
			return convertToDTO(saved);
		} else {
			log.warn("SpecializationService::updateSpecialization:Specialization not found for ID: {}", specId);
			throw new CustomException("Specialization Not Found for Id: " + specId);
		}

	}

	// deleteSpecializationById
	public void deleteById(String specId) {
		log.info("SpecializationService::deleteById:Entry for Specialization ID: {}", specId);

		if (specializationRepo.existsById(specId)) {
			specializationRepo.deleteById(specId);
			log.info("SpecializationService::deleteById:Specialization deleted successfully for ID: {}", specId);
		} else {
			log.warn("SpecializationService::deleteById:Specialization not found for ID: {}", specId);
			throw new CustomException("Specialization not found with Id: " + specId);
		}

	}

	// deleteAllSpecializations
	public void deleteAllSpecialization() {
		log.info("SpecializationService::deleteAllSpecialization:Entry");

		specializationRepo.deleteAll();
		log.info("SpecializationService::deleteAllSpecialization:All specializations deleted successfully");

	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	// validation

	// findAllSpecializationNames
	public List<String> findAllSpecializationNames() {
		List<Specialization> specializations = specializationRepo.findAll();
		List<String> specializationNames = new ArrayList<String>();

		for (Specialization specialization : specializations) {
			specializationNames.add(specialization.getSpecializationName());
		}

		return specializationNames;
	}

}
