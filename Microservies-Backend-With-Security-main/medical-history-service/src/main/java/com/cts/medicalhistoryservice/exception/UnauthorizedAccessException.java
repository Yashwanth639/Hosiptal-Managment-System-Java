package com.cts.medicalhistoryservice.exception;

public class UnauthorizedAccessException extends RuntimeException{
	public UnauthorizedAccessException(String msg) {
		super(msg);
	}

}
