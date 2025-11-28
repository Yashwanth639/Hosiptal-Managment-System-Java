package com.example.loginservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.example.loginservice.model.ResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

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

	// duplicateEntryException
	@ExceptionHandler(DuplicateEntryException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ResultResponse<Void>> handleDuplicateEntryException(DuplicateEntryException ex) {
		log.warn("Duplicate Entry encountered: {}", ex.getMessage());
		ResultResponse<Void> response = ResultResponse.<Void>builder().message(ex.getMessage()).success(false)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	// resourceNotFoundException
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ResultResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
		log.warn("Resource not found: {}", ex.getMessage());
		ResultResponse<Void> response = ResultResponse.<Void>builder().message(ex.getMessage()).success(false)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	// illegalArgumentException
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ResultResponse<Void>> IllegalArgumentException(IllegalArgumentException ex) {
		log.warn("Appointment already cancelled: {}", ex.getMessage());
		ResultResponse<Void> response = ResultResponse.<Void>builder().message(ex.getMessage()).success(false)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// unauthorizedAccessException
	@ExceptionHandler(UnauthorizedAccessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ResultResponse<Void>> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
		log.warn("Unauthorized Access", ex.getMessage());
		ResultResponse<Void> response = ResultResponse.<Void>builder().message(ex.getMessage()).success(false)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// validationException
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ResultResponse<Void>> handleValidationException(ValidationException ex) {
		log.warn("Validation Exception arose", ex.getMessage());
		ResultResponse<Void> response = ResultResponse.<Void>builder().message(ex.getMessage()).success(false)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// customException
	@ExceptionHandler(CustomException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ResultResponse<Void>> handleCustomException(CustomException ex, WebRequest request) {
		log.error("Custom Exception is being thrown : {}", ex.getMessage(), ex);
		ResultResponse<Void> response = ResultResponse.<Void>builder().message("Global Exception: " + ex.getMessage())
				.data(null).success(false).timestamp(LocalDateTime.now()).build();
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
	public ResponseEntity<ResultResponse<Void>> handleGlobalException(Exception ex) {
		log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
		ResultResponse<Void> response = ResultResponse.<Void>builder().message(ex.getMessage()).success(false)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}