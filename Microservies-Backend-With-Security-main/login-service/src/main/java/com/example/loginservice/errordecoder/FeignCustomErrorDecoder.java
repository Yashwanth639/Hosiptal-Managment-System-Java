package com.example.loginservice.errordecoder;

import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.loginservice.exception.CustomException;

@Component
public class FeignCustomErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		// Extract the response body for error details
		String errorMessage = "";
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().asInputStream()))) {
			errorMessage = reader.lines().collect(Collectors.joining());
		} catch (Exception e) {
			errorMessage = "Unknown error occurred while processing response.";
		}

		// Handle specific HTTP status codes
		if (response.status() == 404) {
			return new CustomException("Error from DOCTOR-SERVICE: " + errorMessage);
		}

		// For other status codes, fallback to default behavior
		return new Default().decode(methodKey, response);
	}
}