package com.example.doctorservice.repository;

import com.example.doctorservice.enums.Session;
import com.example.doctorservice.model.Doctor;
import com.example.doctorservice.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, String> {
	// Fetch available slots by doctor and date
	@Query("SELECT da FROM DoctorAvailability da WHERE da.doctor.doctorId = :doctorId AND da.availableDate = :date")
	List<DoctorAvailability> findAvailableSlotsByDoctorIdAndDate(@Param("doctorId") String doctorId,
			@Param("date") LocalDate date);

	// Fetch available slots by session
	@Query("SELECT da FROM DoctorAvailability da WHERE da.session = :session AND da.isAvailable = 1")
	List<DoctorAvailability> findAvailableSlotsBySession(@Param("session") String session);

	// Fetch availability within a date range
	@Query("SELECT da FROM DoctorAvailability da WHERE da.availableDate BETWEEN :startDate AND :endDate")
	List<DoctorAvailability> findAvailabilityInDateRange(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	// findDoctorByDateAndSession
	@Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.availableDate = :availabilityDate AND da.session = :session")
	Optional<DoctorAvailability> findByDoctorAndDateAndSession(@Param("doctor") Doctor doctor,
			@Param("availabilityDate") LocalDate availabilityDate, @Param("session") Session session);

	// getDoctorSchedule
	@Query("SELECT da FROM DoctorAvailability da JOIN da.doctor d WHERE d.doctorId = :doctorId")
	List<DoctorAvailability> getDoctorSchedule(@Param("doctorId") String doctorId);

	// findDoctorByDoctorIdAndAvailableDateAndSessionAndIsAvailable
	@Query("SELECT da FROM DoctorAvailability da WHERE da.doctor.doctorId = :doctorId AND da.availableDate = :availableDate AND da.session = :session AND da.isAvailable = :isAvailable")
	Optional<DoctorAvailability> findDoctorByDoctorIdAndAvailableDateAndSessionAndIsAvailable(String doctorId,
			LocalDate availableDate, Session session, int isAvailable);

	// findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailable
	@Query("select da from DoctorAvailability da "
			+ "where da.doctor.doctorId = :doctorId and da.availableDate = :appointmentDate "
			+ "and da.session = :session and da.isAvailable = :isAvailable")
	Optional<DoctorAvailability> findDoctorAvailabilityByDoctorIdAndAppointmentDateAndSessionAndIsAvailable(
			@Param("doctorId") String doctorId, @Param("appointmentDate") LocalDate appointmentDate,
			@Param("session") Session session, @Param("isAvailable") int isAvailable);

	// existsByDoctorAndAvailableDateAndSession
	@Query("SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END " + "FROM DoctorAvailability d "
			+ "WHERE d.doctor = :doctor AND d.availableDate = :availableDate AND d.session = :session")
	boolean existsByDoctorAndAvailableDateAndSession(@Param("doctor") Doctor doctor,
			@Param("availableDate") LocalDate availableDate, @Param("session") Session session);

	// using Spring Data JPA method naming conventions
	// findAvailableDoctorsBySpecializationNameAndAvailableDateAndSessionAndIsAvailable
	List<DoctorAvailability> findBySpecialization_SpecializationNameAndAvailableDateAndSessionAndIsAvailable(
			String specializationName, LocalDate availableDate, Session session, int isAvailable);

	boolean existsByAvailableDate(LocalDate availableDate);

}
