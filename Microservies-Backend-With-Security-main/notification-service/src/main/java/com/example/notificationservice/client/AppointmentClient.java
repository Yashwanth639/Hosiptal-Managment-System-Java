package com.example.notificationservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.notificationservice.security.FeignClientConfiguration;
import com.example.notificationservice.dto.SendReminderDto;
import com.example.notificationservice.entity.ResultResponse;

import jakarta.validation.Valid;

/**
 * Feign client interface for interacting with the Appointment Service.
 * 
 * @FeignClient(name="NOTIFICATION-APPOINTMENT-SERVICE") Used to define the
 *                                                       client and its target
 *                                                       microservice name.
 */
@FeignClient(name = "APPOINTMENT-SERVICE",configuration = FeignClientConfiguration.class)
public interface AppointmentClient {

	/**
	 * Retrieves appointments for a specific date from the Appointment Service.
	 *
	 * @param appointmentDate The date of the appointments to be retrieved
	 *                        (formatted as a String).
	 * @return ResponseEntity containing a ResultResponse with a list of
	 *         SendReminderDto instances.
	 */
	// findAppointmentsByAppointmentDate
	@GetMapping("/api/appointments/date/{appointmentDate}")
	ResponseEntity<ResultResponse<List<SendReminderDto>>> findAppointmentsByAppointmentDate(
			@Valid @PathVariable String appointmentDate);

}
