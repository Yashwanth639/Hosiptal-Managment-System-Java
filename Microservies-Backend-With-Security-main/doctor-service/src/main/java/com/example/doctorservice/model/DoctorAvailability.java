package com.example.doctorservice.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.UUID;

import com.example.doctorservice.enums.Session;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
@Table(name = "doctoravailability")
public class DoctorAvailability {
	// Primary Key
	@Id
	// @GeneratedValue(strategy=GenerationType.UUID)
	@Column(name = "availabilityId")
	private String availabilityId;

	@Column(name = "availableDate", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate availableDate;

	@Column(name = "session", nullable = false)
	@Enumerated(EnumType.STRING)
	private Session session;

	@Column(name = "isAvailable", nullable = false)
	private int isAvailable;

	@Column(name = "createdAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(name = "updatedAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;

	@PrePersist
	public void insert() {
		this.availabilityId = UUID.randomUUID().toString();
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// Foreign Key Relation Definitions
	@ManyToOne
	@JoinColumn(name = "doctorId", nullable = false)
	private Doctor doctor;

	@ManyToOne
	@JoinColumn(name = "specializationId", nullable = false)
	private Specialization specialization;

}