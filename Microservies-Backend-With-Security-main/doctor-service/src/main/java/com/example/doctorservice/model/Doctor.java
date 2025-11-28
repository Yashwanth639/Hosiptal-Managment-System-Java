package com.example.doctorservice.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "doctor")
public class Doctor {
	// Primary Key
	@Id
	@Column(name = "doctorId")
	private String doctorId;

	// Local Attributes
	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "contactDetails", nullable = false)
	private String contactDetails;

	@Column(name = "createdAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(name = "updatedAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;
	
	@Column(name="gender", nullable=false)
	private String gender;

	@PrePersist
	public void insert() {
		// this.doctorId = UUID.randomUUID().toString();
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// Foreign Key Definitions
	@ManyToOne
	@JoinColumn(name = "specializationId", nullable = false)
	private Specialization specialization;

	@OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
	// @JsonIgnore
	private List<DoctorAvailability> doctorAvailability;

}