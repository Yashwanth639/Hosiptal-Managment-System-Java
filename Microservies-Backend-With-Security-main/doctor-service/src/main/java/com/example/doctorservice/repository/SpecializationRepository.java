package com.example.doctorservice.repository;

import com.example.doctorservice.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, String> {

	// Fetch specialization by name using @Query
	@Query("SELECT s FROM Specialization s WHERE LOWER(s.specializationName) = LOWER(:specializationName)")
	Specialization findBySpecializationName(String specializationName);

	boolean existsBySpecializationName(String specializationName);

}
