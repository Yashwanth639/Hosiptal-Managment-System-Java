package com.example.notificationservice.exception;

public class UnauthorizedAccessException extends RuntimeException{
	public UnauthorizedAccessException(String msg) {
		super(msg);
	}

}
