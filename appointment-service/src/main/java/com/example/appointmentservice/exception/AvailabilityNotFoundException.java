package com.example.appointmentservice.exception;

public class AvailabilityNotFoundException extends RuntimeException {
	public AvailabilityNotFoundException(String message) {
		super(message);
	}

}
