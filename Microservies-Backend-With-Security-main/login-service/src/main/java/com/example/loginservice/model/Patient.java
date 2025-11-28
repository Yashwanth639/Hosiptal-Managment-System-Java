package com.example.loginservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "patient") // Specify the table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
	// Primary Key
	@Id
	@Column(name = "patientId")
	private String patientId;

	// Local Attributes
	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "dateOfBirth", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	@Column(name="gender",nullable=false)
	private String gender;
	
	@Column(name="heightInCm")
	private Integer heightInCm;
	
	@Column(name="weightInCm")
	private Integer weightInCm;

	@Column(name = "contactDetails", nullable = false)
	private String contactDetails;

	@Column(name = "createdAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(name = "updatedAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now(); // No need to manually assign patientId
		this.updatedAt = LocalDateTime.now();
		// this.patientId = UUID.randomUUID().toString();
	}

	@PreUpdate
	public void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// Foreign Key Relations

	@OneToOne
	@MapsId // Ensures patientId is the same as userId
	@JoinColumn(name = "patientId", nullable = false, unique = true) // Foreign key to user table
	private User user;

}
