package com.cts.medicalhistoryservice.exception;

public class MedicalRecordNotFoundException extends RuntimeException {
	public MedicalRecordNotFoundException(String message) {
		super(message);
	}
}
