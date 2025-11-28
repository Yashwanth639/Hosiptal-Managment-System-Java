package com.example.loginservice.exception;

public class DuplicateEntryException extends RuntimeException {
	public DuplicateEntryException(String msg) {
		super(msg);
	}

}
