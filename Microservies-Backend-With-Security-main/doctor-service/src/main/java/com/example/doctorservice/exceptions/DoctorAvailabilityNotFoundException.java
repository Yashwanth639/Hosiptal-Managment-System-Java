package com.example.doctorservice.exceptions;

public class DoctorAvailabilityNotFoundException extends RuntimeException {
	public DoctorAvailabilityNotFoundException(String message) {
		super(message);
	}
}