package com.example.doctorservice.scheduler;

import com.example.doctorservice.enums.Session;

import com.example.doctorservice.model.Doctor;

import com.example.doctorservice.model.DoctorAvailability;

import com.example.doctorservice.model.Specialization;

import com.example.doctorservice.repository.DoctorAvailabilityRepository;

import com.example.doctorservice.repository.DoctorRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.List;

import java.util.UUID;

@Component
@Slf4j
public class AvailabilityScheduler {

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private DoctorAvailabilityRepository doctorAvailabilityRepository;

	private boolean initialScheduleGenerated = false; // Flag for initial generation

	// generateDoctorAvailabilitySlotsScheduler
	@Scheduled(cron = "0 54 13  * * *")
	@Transactional
	public void generateDoctorAvailabilitySlots() {
		log.info("Starting doctor availability slot generation...");

		List<Doctor> doctors = doctorRepository.findAll();
		LocalDate startDate = LocalDate.now();

		if (!initialScheduleGenerated) {
			// Generate initial 90-day schedule
			for (int i = 0; i < 90; i++) {
				LocalDate date = startDate.plusDays(i);
				for (Doctor doctor : doctors) {
					generateSlotsForDoctor(doctor, date);
				}
			}
			initialScheduleGenerated = true; // Set flag after initial generation
			log.info("Initial doctor availability slot generation for 90 days completed.");
		} else {
			// Generate slots for the 91st day (rolling)
			LocalDate newDate = startDate.plusDays(90);
			for (Doctor doctor : doctors) {
				generateSlotsForDoctor(doctor, newDate);
			}
			log.info("Doctor availability slot generation for the 91st day (rolling) completed.");
		}
	}

	// generateSlotsForDoctor
	private void generateSlotsForDoctor(Doctor doctor, LocalDate date) {
		Specialization specialization = doctor.getSpecialization();

		generateSlot(doctor, specialization, date, Session.FN);
		generateSlot(doctor, specialization, date, Session.AN);
	}

	// generateSlot
	private void generateSlot(Doctor doctor, Specialization specialization, LocalDate date, Session session) {
		// Check if a slot for the same doctor, date, and session already exists
		boolean exists = doctorAvailabilityRepository.existsByDoctorAndAvailableDateAndSession(doctor, date, session);

		if (exists) {
			log.info("Slot already exists for doctor: {}, date: {}, session: {}", doctor.getDoctorId(), date, session);
			return; // Skip creation
		}

		// Create a new availability slot if no duplicate exists
		DoctorAvailability availability = new DoctorAvailability();
		availability.setAvailabilityId(UUID.randomUUID().toString());
		availability.setDoctor(doctor);
		availability.setSpecialization(specialization);
		availability.setAvailableDate(date);
		availability.setSession(session);
		availability.setIsAvailable(1);

		doctorAvailabilityRepository.save(availability);

		log.info("Generated slot for doctor: {}, date: {}, session: {}", doctor.getDoctorId(), date, session);
	}
}
