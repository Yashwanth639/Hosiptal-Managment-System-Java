package com.example.doctorservice.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "specialization")
public class Specialization {

	// Primary Key
	@Id
	// @GeneratedValue(strategy=GenerationType.UUID)
	@Column(name = "specializationId")
	private String specializationId;

	@Column(name = "specializationName", nullable = false, unique = true)
	private String specializationName;

	@Column(name = "createdAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(name = "updatedAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;

	@PrePersist
	public void insert() {
		this.specializationId = UUID.randomUUID().toString();
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// Foreign Key Relation Definitions

	@OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Doctor> doctor;

	@OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL)
	private List<DoctorAvailability> doctorAvailability;
}