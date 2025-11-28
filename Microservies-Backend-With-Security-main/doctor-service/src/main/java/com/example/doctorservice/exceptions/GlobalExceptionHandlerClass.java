package com.example.doctorservice.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.doctorservice.model.ResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerClass {

	// methodArgumentNotValidException
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ResultResponse<Map<String, String>>> handleValidationExceptions(
			MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors().forEach(error -> {
			errors.put(error.getField(), error.getDefaultMessage());
		});

		ResultResponse<Map<String, String>> response = ResultResponse.<Map<String, String>>builder()
				.message("Validation failed").data(errors).success(false).timestamp(LocalDateTime.now()).build();

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// doctorNotFoundException
	@ExceptionHandler(DoctorNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ResultResponse<String>> handleDoctorNotFoundException(DoctorNotFoundException ex) {
		// ex.printStackTrace();
		log.warn("DoctorNotFoundException: {}", ex.getMessage());
		ResultResponse<String> response = ResultResponse.<String>builder().message(ex.getMessage()).success(false)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	// doctorAvailabilityNotFoundException
	@ExceptionHandler(DoctorAvailabilityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ResultResponse<String>> handleDoctorAvailabilityNotFoundException(
			DoctorAvailabilityNotFoundException ex) {
		log.warn("DoctorAvailabilityNotFoundException: {}", ex.getMessage());
		ResultResponse<String> response = ResultResponse.<String>builder().message(ex.getMessage()).success(false)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	// customException
	@ExceptionHandler(CustomException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ResultResponse<Void>> handleCustomException(CustomException ex, WebRequest request) {
		log.error("Custom Exception is being thrown : {}", ex.getMessage(), ex);
		ResultResponse<Void> response = ResultResponse.<Void>builder().message(ex.getMessage()).data(null)
				.success(false).timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	// feignException
	@ExceptionHandler(feign.FeignException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ResultResponse<Map<String, Object>>> handleFeignException(feign.FeignException ex) {
		log.error("Feign Client Exception: {}", ex.getMessage(), ex);

		// Initialize parsed error message
		Map<String, Object> parsedError = new HashMap<>();

		try {
			// Extract the JSON body from the Feign exception
			String responseBody = ex.contentUTF8();
			if (responseBody != null && !responseBody.isEmpty()) {
				ObjectMapper objectMapper = new ObjectMapper();
				parsedError = objectMapper.readValue(responseBody, Map.class);
			} else {
				parsedError.put("message", "Unknown error occurred in Feign Client.");
			}
		} catch (Exception e) {
			log.error("Failed to parse Feign error response body.", e);
			parsedError.put("message", "Error occurred but response body could not be parsed.");
		}

		// Wrap the parsed error into your custom response
		ResultResponse<Map<String, Object>> response = ResultResponse.<Map<String, Object>>builder()
				.message("Feign Client Error").data(parsedError).success(false).timestamp(LocalDateTime.now()).build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	// exception
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ResultResponse<String>> handleGenericException(Exception ex) {
		ex.printStackTrace();
		log.error("Unexpected error occurred: {}", ex.getMessage());
		ResultResponse<String> response = ResultResponse.<String>builder().message(ex.getMessage()).success(false)
				.timestamp(LocalDateTime.now()).build();
//        ex.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}
