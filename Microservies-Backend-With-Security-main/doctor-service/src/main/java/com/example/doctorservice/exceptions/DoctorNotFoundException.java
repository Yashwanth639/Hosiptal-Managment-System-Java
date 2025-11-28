package com.example.doctorservice.exceptions;

public class DoctorNotFoundException extends RuntimeException {

	public DoctorNotFoundException(String message) {
		super(message);
	}
}
