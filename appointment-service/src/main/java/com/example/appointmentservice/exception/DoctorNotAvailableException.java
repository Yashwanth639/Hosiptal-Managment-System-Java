package com.example.appointmentservice.exception;

public class DoctorNotAvailableException extends RuntimeException {
	public DoctorNotAvailableException(String message) {
		super(message);
	}
}
