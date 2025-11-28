package com.example.doctorservice.security;


import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.doctorservice.model.Specialization;
import com.example.doctorservice.repository.SpecializationRepository;

@Slf4j
@Component
public class SpecializationInitializer implements CommandLineRunner{
	
	@Autowired
    private SpecializationRepository specializationRepository;

    @Override
    public void run(String... args) {
    	createSpecializationIfNotFound("Cardiology");
    	createSpecializationIfNotFound("Neurology");
    	createSpecializationIfNotFound("Pediatrics");
    	createSpecializationIfNotFound("Dermatology");
    	createSpecializationIfNotFound("Orthopedics");
    	createSpecializationIfNotFound("Psychiatry");
    	createSpecializationIfNotFound("General Medicine");
    	createSpecializationIfNotFound("Surgery");
    	createSpecializationIfNotFound("Emergency");
    	createSpecializationIfNotFound("Gynaecology");
    }

    private void createSpecializationIfNotFound(String specializationName) {
        try {
            // Check if specialization already exists
            Specialization specialization = specializationRepository.findBySpecializationName(specializationName);
            if (specialization == null) {
                // Create and save the new specialization
                specialization = new Specialization();
                specialization.setSpecializationName(specializationName);
                specializationRepository.save(specialization);
                log.info("Specialization created: {}", specializationName);
            } else {
                log.info("Specialization already exists: {}", specializationName);
            }
        } catch (Exception e) {
            log.error("Error initializing specialization: {} - {}", specializationName, e.getMessage());
        }
    }
}







    

