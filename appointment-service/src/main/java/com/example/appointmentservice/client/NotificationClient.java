package com.example.appointmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.appointmentservice.dto.BookAppointmentAndCancelNotificationDto;

import com.example.appointmentservice.dto.RescheduleNotificationDto;

import com.example.appointmentservice.model.ResultResponse;
import com.example.appointmentservice.security.FeignClientConfiguration;

import jakarta.validation.Valid;

@FeignClient(name = "NOTIFICATION-SERVICE",configuration = FeignClientConfiguration.class)
public interface NotificationClient {

	// cancelAppointmentNotificationInsertion
	@PostMapping("/notification/cancelAppointmentInsertion")
	public ResponseEntity<ResultResponse<Void>> insertNotificationsForCancelAppointment(
			@Valid @RequestBody BookAppointmentAndCancelNotificationDto bookAndCancelNotificationDto);

	// bookAppointmentNotificationInsertion
	@PostMapping("/notification/bookAppointmentInsertion")
	public ResponseEntity<ResultResponse<String>> insertNotificationsForBookAppointment(
			@Valid @RequestBody BookAppointmentAndCancelNotificationDto bookAndCancelNotificationDto);

	// rescheduleAppointmentNotificationInsertion
	@PostMapping("/notification/rescheduleAppointmentInsertion")
	public ResponseEntity<ResultResponse<String>> insertNotificationsForRescheduleAppointment(
			@Valid @RequestBody RescheduleNotificationDto rescheduleNotificationDto);

}
