package com.cts.medicalhistoryservice.exception;

public class DoctorIdNotFoundException extends RuntimeException {
	public DoctorIdNotFoundException(String message) {
		super(message);
	}
}
