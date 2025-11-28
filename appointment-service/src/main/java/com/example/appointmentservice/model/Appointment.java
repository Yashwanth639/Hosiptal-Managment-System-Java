package com.example.appointmentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.appointmentservice.enums.AppointmentStatus;
import com.example.appointmentservice.enums.Session;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "appointment")
@Builder
public class Appointment {

	// Primary Key
	@Id
	@Column(name = "appointmentId")
	private String appointmentId;

	@Column(name = "appointmentDate", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate appointmentDate;

	@Column(name = "session", nullable = false)
	@Enumerated(EnumType.STRING)
	private Session session;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private AppointmentStatus appointmentstatus;

	@Column(name = "createdAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(name = "updatedAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;

	@PrePersist
	public void insert() {
		this.appointmentId = UUID.randomUUID().toString();
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// Foreign Key Relation Definitions
	@Column(name = "patientId")
	private String patientId;

	@Column(name = "doctorId", nullable = false)
	private String doctorId;

	@Column(name = "availabilityId", nullable = false)
	private String availabilityId;

}
