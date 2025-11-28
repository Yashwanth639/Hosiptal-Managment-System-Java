package com.example.appointmentservice.exception;

public class AppointmentAlreadyCancelledException extends RuntimeException {
	public AppointmentAlreadyCancelledException(String message) {
		super(message);
	}
}
