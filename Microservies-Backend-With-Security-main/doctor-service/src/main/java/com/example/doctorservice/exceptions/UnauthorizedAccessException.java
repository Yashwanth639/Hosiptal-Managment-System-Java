package com.example.doctorservice.exceptions;

public class UnauthorizedAccessException  extends RuntimeException{
	public UnauthorizedAccessException(String msg) {
		super(msg);
	}

}
