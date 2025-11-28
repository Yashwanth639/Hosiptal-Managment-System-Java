package com.example.doctorservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.example.doctorservice.dto.NotificationDto;
import com.example.doctorservice.model.ResultResponse;
import com.example.doctorservice.security.FeignClientConfiguration;

import jakarta.validation.Valid;

@FeignClient(name = "NOTIFICATION-SERVICE",configuration = FeignClientConfiguration.class)
public interface NotificationClient {

	@GetMapping("/notification/doctorProfile/{doctorId}")
	public ResponseEntity<ResultResponse<List<NotificationDto>>> getDoctorNotificationsByDoctorId(
			@Valid @PathVariable String doctorId);
	
	@PutMapping("/notification/markAsRead/{notificationId}")
	public ResponseEntity<ResultResponse<Void>> markNotificationAsRead(@Valid @PathVariable String notificationId);

}
