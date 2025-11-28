package com.example.loginservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.example.loginservice.config.FeignClientConfiguration;
import com.example.loginservice.dto.NotificationDto;
import com.example.loginservice.model.ResultResponse;

import jakarta.validation.Valid;

@FeignClient(name = "NOTIFICATION-SERVICE",configuration = FeignClientConfiguration.class)
public interface NotificationClient {

	@GetMapping("/notification/patientProfile/{patientId}")
	public ResponseEntity<ResultResponse<List<NotificationDto>>> getPatientNotificationsByPatientId(
			@Valid @PathVariable String patientId);
	
	@PutMapping("/notification/markAsRead/{notificationId}")
	public ResponseEntity<ResultResponse<Void>> markNotificationAsRead(@Valid @PathVariable String notificationId);

}
