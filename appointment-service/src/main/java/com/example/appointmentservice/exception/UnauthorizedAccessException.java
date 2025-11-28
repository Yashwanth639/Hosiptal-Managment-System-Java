package com.example.appointmentservice.exception;

public class UnauthorizedAccessException extends RuntimeException{
	public UnauthorizedAccessException(String msg) {
		super(msg);
	}

}
