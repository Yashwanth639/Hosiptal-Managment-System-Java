package com.cts.medicalhistoryservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.medicalhistoryservice.dto.AppointmentDetailsDto;
import com.cts.medicalhistoryservice.entity.ResultResponse;
import com.cts.medicalhistoryservice.security.FeignClientConfiguration;

import jakarta.validation.Valid;

@FeignClient(name = "APPOINTMENT-SERVICE",configuration = FeignClientConfiguration.class)
public interface AppointmentClient {

	// findAppointmentByAppointmentId
	@GetMapping("/api/appointments/{appointmentId}")
	public ResponseEntity<ResultResponse<AppointmentDetailsDto>> findById(@Valid @PathVariable String appointmentId);

}
