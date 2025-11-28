package com.cts.medicalhistoryservice.exception;

public class PatientIdNotFoundException extends RuntimeException {
	public PatientIdNotFoundException(String message) {
		super(message);
	}

}
