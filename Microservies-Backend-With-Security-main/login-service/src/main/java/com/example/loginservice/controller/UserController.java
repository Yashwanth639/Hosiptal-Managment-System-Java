package com.example.loginservice.controller;

import com.example.loginservice.dto.LoginDto;
import com.example.loginservice.dto.UserDto;
import com.example.loginservice.dto.UserRegisterDoctorDto;
import com.example.loginservice.dto.UserRegisterPatientDto;
import com.example.loginservice.model.ResultResponse;
import com.example.loginservice.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

//import com.example.loginservice.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

	@Autowired
	private UserService userService;

	// getAllUsers
	@GetMapping("/getAll")
	public ResponseEntity<ResultResponse<List<UserDto>>> findAll() {
		log.info("UserController::findAll:Entry");

		List<UserDto> users = userService.findAll();

		ResultResponse<List<UserDto>> resultResponse = ResultResponse.<List<UserDto>>builder().success(true)
				.message("Users retrieved successfully").data(users).timestamp(LocalDateTime.now()).build();

		log.info("UserController::findAll:Exit successfully");

		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// getUserById
	@GetMapping("/{userId}")
	public ResponseEntity<ResultResponse<UserDto>> findById(@Valid @PathVariable String userId) {
		log.info("UserController::findById:Entry for User ID: {}", userId);

		UserDto user = userService.findById(userId);

		ResultResponse<UserDto> resultResponse = ResultResponse.<UserDto>builder().success(true)
				.message("User retrieved successfully").data(user).timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("UserController::findById:Exit successfully for User ID: {}", userId);

		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// addUser
	@PostMapping("/add")
	public ResponseEntity<ResultResponse<UserDto>> addUser(@Valid @RequestBody UserDto uDto) {
		log.info("UserController::addUser:Entry with data: {}", uDto);

		UserDto user = userService.saveUser(uDto);

		ResultResponse<UserDto> resultResponse = ResultResponse.<UserDto>builder().success(true)
				.message("User added successfully").data(user).timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("UserController::addUser:Exit successfully");

		return new ResponseEntity<>(resultResponse, HttpStatus.CREATED);
	}

	// userLogin

	  @PostMapping("/login")
	    public ResponseEntity<String> loginUser(@RequestBody LoginDto request) {
	        try {
	            // Call the service method to authenticate and generate token
	            String token = userService.userLogin(request.getEmail(), request.getPasswordHash(),request.getRole());
	            return new ResponseEntity<>(token, HttpStatus.OK);
	        } catch (Exception e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
	        }
	    }

	// updateUserById
	@PutMapping("/update/{userId}")
	public ResponseEntity<ResultResponse<UserDto>> updateUser(@Valid @PathVariable String userId,
			@Valid @RequestBody UserDto uDto) {
		log.info("UserController::updateUser:Entry for User ID: {}", userId);

		UserDto user = userService.updateUser(userId, uDto);

		ResultResponse<UserDto> resultResponse = ResultResponse.<UserDto>builder().success(true)
				.message("User updated successfully").data(user).timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("UserController::updateUser:Exit successfully for User ID: {}", userId);

		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// deleteUserById
	@DeleteMapping("/delete/{userId}")
	public ResponseEntity<ResultResponse<Void>> deleteById(@Valid @PathVariable String userId) {
		log.info("UserController::deleteById:Entry for User ID: {}", userId);

		userService.deleteById(userId);

		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("User deleted successfully").data(null).timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("UserController::deleteById:Exit successfully for User ID: {}", userId);

		return new ResponseEntity<>(resultResponse, HttpStatus.NO_CONTENT);
	}

	// deleteAllUsers
	@DeleteMapping("/deleteAll")
	public ResponseEntity<ResultResponse<Void>> deleteAllUsers() {
		log.info("UserController::deleteAllUsers:Entry");

		userService.deleteAllUsers();

		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("All users deleted successfully").data(null).timestamp(LocalDateTime.now(ZoneOffset.UTC))
				.build();

		log.info("UserController::deleteAllUsers:Exit successfully");

		return new ResponseEntity<>(resultResponse, HttpStatus.NO_CONTENT);
	}

	// ----------------------------------------------------------------------------------------------------------------------------

	// patientRegistration
	@PostMapping("/register/patient") // --> working
	public ResponseEntity<ResultResponse<UserRegisterPatientDto>> registerPatient(
			@Valid @RequestBody UserRegisterPatientDto uRDto) {
		UserRegisterPatientDto patient = userService.registerPatient(uRDto);
		ResultResponse<UserRegisterPatientDto> response = ResultResponse.<UserRegisterPatientDto>builder().data(patient)
				.success(true).message("Patient Registration successful").timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// doctorRegistration
	@PostMapping("/register/doctor") // --> working
	public ResponseEntity<ResultResponse<UserRegisterDoctorDto>> registerDoctor(
			@Valid @RequestBody UserRegisterDoctorDto uRDto) {

		UserRegisterDoctorDto doctor = userService.registerDoctor(uRDto);
		ResultResponse<UserRegisterDoctorDto> response = ResultResponse.<UserRegisterDoctorDto>builder().data(doctor)
				.success(true).message("Doctor Registration successful").timestamp(LocalDateTime.now()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
