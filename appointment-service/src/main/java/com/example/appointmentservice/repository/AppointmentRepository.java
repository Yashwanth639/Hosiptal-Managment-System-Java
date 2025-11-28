package com.example.appointmentservice.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.appointmentservice.enums.AppointmentStatus;
import com.example.appointmentservice.enums.Session;
import com.example.appointmentservice.model.Appointment;

import jakarta.validation.Valid;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {

	/**
	 * Finds current appointments for a given patient.
	 */
	// findCurrentPatientAppointments
	@Query("SELECT a FROM Appointment a " + "WHERE a.patientId=:patientId AND a.appointmentDate>=:currentDate "
			+ "AND a.appointmentstatus=:appointmentstatus")
	List<Appointment> findCurrentPatientAppointments(@Valid @Param("patientId") String patientId,
			@Param("currentDate") LocalDate currentDate,
			@Param("appointmentstatus") AppointmentStatus appointmentstatus);

	/**
	 * Finds current appointments for a given doctor.
	 */
	// findCurrentDoctorAppointments
	@Query("SELECT a FROM Appointment a " + "WHERE a.doctorId=:doctorId AND a.appointmentDate>=:currentDate "
			+ "AND a.appointmentstatus=:appointmentstatus")
	List<Appointment> findCurrentDoctorAppointments(@Param("doctorId") String doctorId,
			@Param("currentDate") LocalDate currentDate,
			@Param("appointmentstatus") AppointmentStatus appointmentstatus);

	/**
	 * Finds past appointments for a given patient.
	 */
	// findPastPatientAppointments
	@Query("SELECT a FROM Appointment a " + "WHERE a.patientId=:patientId AND a.appointmentDate<=:currentDate "
			+ "AND a.appointmentstatus=:appointmentstatus")
	List<Appointment> findPastPatientAppointments(@Param("patientId") String patientId,
			@Param("currentDate") LocalDate currentDate,
			@Param("appointmentstatus") AppointmentStatus appointmentstatus);

	/**
	 * Finds past appointments for a given doctor.
	 */
	// findPastDoctorAppointments
	@Query("SELECT a FROM Appointment a " + "WHERE a.doctorId=:doctorId AND a.appointmentDate<=:currentDate "
			+ "AND a.appointmentstatus=:appointmentstatus")
	List<Appointment> findPastDoctorAppointments(@Param("doctorId") String doctorId,
			@Param("currentDate") LocalDate currentDate,
			@Param("appointmentstatus") AppointmentStatus appointmentstatus);

	// checkExistingAppointmentByPatientIdAndAppointmentDateAndSessionAndStatus
	@Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a "
			+ "WHERE a.patientId = :patientId AND a.appointmentDate = :appointmentDate "
			+ "AND a.session = :session AND a.appointmentstatus = :appointmentstatus")
	boolean checkExistingAppointmentByPatientIdAndAppointmentDateAndSessionAndStatus(
			@Param("patientId") String patientId, @Param("appointmentDate") LocalDate appointmentDate,
			@Param("session") Session session, @Param("appointmentstatus") AppointmentStatus appointmentstatus);

	// findAppointmentsByAppointmentDate
	@Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :appointmentDate")
	List<Appointment> findAppointmentsByAppointmentDate(@Param("appointmentDate") LocalDate appointmentDate);

	// findAppointmentsWithinDateRange
	@Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate")
	List<Appointment> findAppointmentsInDateRange(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);
}
