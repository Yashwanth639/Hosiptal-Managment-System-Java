package com.example.loginservice.exception;

public class ValidationException extends RuntimeException {
	public ValidationException(String message) {
		super(message);
	}
}