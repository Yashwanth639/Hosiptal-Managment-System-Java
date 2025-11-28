package com.example.loginservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.loginservice.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
	
	boolean existsByContactDetails(String contactDetails);
}
