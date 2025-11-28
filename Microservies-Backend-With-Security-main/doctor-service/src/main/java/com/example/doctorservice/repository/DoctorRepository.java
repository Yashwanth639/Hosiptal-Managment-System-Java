package com.example.doctorservice.repository;

import com.example.doctorservice.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {
	// Fetch doctors by specialization ID
	@Query("SELECT d FROM Doctor d WHERE d.specialization.specializationId = :specializationId")
	List<Doctor> findDoctorsBySpecialization(@Param("specializationId") String specializationId);

	// getDoctorsListBySpecializationName
	@Query("SELECT d FROM Doctor d JOIN d.specialization s WHERE s.specializationName = :specializationName")
	List<Doctor> getDoctorsListBySpecializationName(@Param("specializationName") String specializationName);

}
