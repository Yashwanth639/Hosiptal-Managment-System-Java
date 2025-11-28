package com.cts.medicalhistoryservice.repository;

import com.cts.medicalhistoryservice.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, String> {

	// findMedicalDetailsByPatientId
	@Query("SELECT m FROM MedicalHistory m WHERE m.patientId = :pId")
	List<MedicalHistory> findByPatientId(@Param("pId") String pId);

//	// findMedicalDetailsOfPatientWithinDateRange --> verify this functionality
//	// again...
//	@Query("SELECT m FROM MedicalHistory m WHERE DATE(m.dateOfVisit) BETWEEN :startDate AND :endDate AND m.patientId = :pId")
//	List<MedicalHistory> getMedicalDetailsByPatientIdAndDateVisit(@Param("pId") String pId,
//			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	// findMedicalDetailsByDoctorId
	@Query("SELECT m FROM MedicalHistory m WHERE m.doctorId = :dId")
	List<MedicalHistory> findByDoctorId(@Param("dId") String dId);

	// Fetch medicalHistory within a date range
	@Query("SELECT m FROM MedicalHistory m WHERE m.dateOfVisit BETWEEN :startDate AND :endDate")
	List<MedicalHistory> findMedicalHistoryInDateRange(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);
}
