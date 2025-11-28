package com.cts.medicalhistoryservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Data
@Table(name = "medicalhistory")
public class MedicalHistory {

	@Id
	@Column(name = "historyId")
	private String historyId;

	@Column(nullable = false)
	private String diagnosis;

	@Column(nullable = false)
	private String treatment;

	@Column(nullable = false)
	private String medications;

	@Column(nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate dateOfVisit; // Date of Visit

	@Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;

	@Column(name = "patientId", nullable = false)
	private String patientId;

	@Column(name = "doctorId", nullable = false)
	private String doctorId;

	@Column(name = "appointmentId", nullable = false)
	private String appointmentId;

	@PrePersist
	public void insert() {
		this.historyId = UUID.randomUUID().toString();
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
	
	@Column(name = "bloodPressure")
	private String bloodPressure;
	
	@Column(name = "heartRate")
	private String heartRate;
	
	@Column(name = "temperature")
	private String temperature;

}
