package com.example.notificationservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.notificationservice.enums.NotificationStatus;
import com.example.notificationservice.enums.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.Id;

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
@Table(name = "notification")
public class Notification {

	// Primary Key
	@Id
	// @GeneratedValue(strategy=GenerationType.UUID)
	@Column(name = "notificationId")
	private String notificationId;

	@Column(name = "notificationType", nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	@Column(name = "message", nullable = false)
	private String message;

	@Column(name = "timeStamp", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime timeStamp;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificationStatus status;

	@Column(name = "createdAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(name = "updatedAt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;

	@PrePersist
	public void insert() {
		this.notificationId = UUID.randomUUID().toString();
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.timeStamp = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// Foreign Key Relation Definitions
	@Column(name = "userId", nullable = false)
	private String userId;

	@Column(name = "appointmentId", nullable = false)
	private String appointmentId;

}
