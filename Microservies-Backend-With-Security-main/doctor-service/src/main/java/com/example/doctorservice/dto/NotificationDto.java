package com.example.doctorservice.dto;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;

import com.example.doctorservice.enums.NotificationStatus;
import com.example.doctorservice.enums.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class NotificationDto {

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private String notificationId;

	@NotBlank(message = "Appointment ID cannot be blank")
	private String appointmentId;

	@NotBlank(message = "User ID cannot be blank")
	private String userId;

	@NotNull(message = "Notification type is required")
//    @Pattern(
//        regexp = "Appointment|Reminder|Reschedule|Cancellation",
//        message = "Invalid notification type. Allowed values: Appointment, Reminder, Reschedule, Cancellation"
//    )
	private NotificationType notificationType;

	@NotNull(message = "Notification status is required")
//    @Pattern(
//        regexp = "Sent|Pending|Read",
//        message = "Invalid notification status. Allowed values: Sent, Pending, Read"
//    )
	private NotificationStatus status;

	@NotBlank(message = "Message cannot be blank")
	@Size(max = 1000, message = "Message must not exceed 1000 characters")
	private String message;

	private LocalDateTime timestamp;
}
