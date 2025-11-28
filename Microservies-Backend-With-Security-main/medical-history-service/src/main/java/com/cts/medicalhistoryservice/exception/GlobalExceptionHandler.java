package com.cts.medicalhistoryservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.cts.medicalhistoryservice.entity.ResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
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

	// patientNotFoundException
	@ExceptionHandler(PatientIdNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ResultResponse<String>> handlePatientIdNotFoundException(PatientIdNotFoundException ex,
			WebRequest request) {
		ResultResponse<String> errorResponse = ResultResponse.<String>builder().success(false).message(ex.getMessage())
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	// doctorNotFoundException
	@ExceptionHandler(DoctorIdNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ResultResponse<String>> handleDoctorIdNotFoundException(DoctorIdNotFoundException ex,
			WebRequest request) {
		ResultResponse<String> errorResponse = ResultResponse.<String>builder().success(false).message(ex.getMessage())
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	// medicalRecordNotFoundException
	@ExceptionHandler(MedicalRecordNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ResultResponse<String>> handleMedicalrecordNotFoundException(
			MedicalRecordNotFoundException ex, WebRequest request) {
		ResultResponse<String> errorResponse = ResultResponse.<String>builder().success(false).message(ex.getMessage())
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	// medicalRecordCreationException
	@ExceptionHandler(MedicalRecordCreationException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ResultResponse<String>> handleMedicalRecordCreationException(
			MedicalRecordCreationException ex, WebRequest request) {
		ResultResponse<String> errorResponse = ResultResponse.<String>builder().success(false).message(ex.getMessage())
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	// customException
	@ExceptionHandler(CustomException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ResultResponse<String>> handleCustomException(CustomException ex, WebRequest request) {
		ResultResponse<String> errorResponse = ResultResponse.<String>builder().success(false).message(ex.getMessage())
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
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
	public ResponseEntity<ResultResponse<String>> handleGlobalException(Exception ex, WebRequest request) {
		ResultResponse<String> errorResponse = ResultResponse.<String>builder().success(false).message(ex.getMessage())
				.timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}