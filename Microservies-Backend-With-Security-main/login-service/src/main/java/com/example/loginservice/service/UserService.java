
package com.example.loginservice.service;

import com.example.loginservice.client.DoctorClient;


import com.example.loginservice.dto.LoginDto;
import com.example.loginservice.dto.UserDto;
import com.example.loginservice.dto.UserRegisterDoctorDto;
import com.example.loginservice.dto.UserRegisterPatientDto;
import com.example.loginservice.exception.CustomException;
import com.example.loginservice.exception.DuplicateEntryException;
import com.example.loginservice.exception.ResourceNotFoundException;
import com.example.loginservice.exception.UnauthorizedAccessException;
import com.example.loginservice.model.Patient;
import com.example.loginservice.model.Role;
import com.example.loginservice.model.User;
import com.example.loginservice.repository.PatientRepository;
import com.example.loginservice.repository.RoleRepository;
import com.example.loginservice.repository.UserRepository;
import com.example.loginservice.exception.UserNotFoundException;
import com.example.loginservice.exception.ValidationException;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private DoctorClient doctorClient;
	
	@Autowired
	private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Lazy
    @Autowired
    private AuthenticationManager authManager;

	// convertEntityToDTO
	private UserDto convertToDTO(User u) {
		UserDto uDto = new UserDto();
		uDto.setCreatedAt(u.getCreatedAt());
		uDto.setUpdatedAt(u.getUpdatedAt());
		uDto.setUserId(u.getUserId());
		uDto.setRoleId(u.getRole().getRoleId());
		uDto.setEmail(u.getEmail());
		uDto.setPasswordHash(u.getPasswordHash());
	

		return uDto;

	}
	
	// convertDTOToEntity
	private User convertToEntity(UserDto uDto) {
		User u = new User();
		u.setCreatedAt(uDto.getCreatedAt());
		u.setUpdatedAt(uDto.getUpdatedAt());
		u.setUserId(uDto.getUserId());

		Role r = roleRepository.findById(uDto.getRoleId()).orElse(null);
		u.setRole(r);

		u.setEmail(uDto.getEmail());
		u.setPasswordHash(uDto.getPasswordHash());

		return u;
	}

	// findAll
	public List<UserDto> findAll() {
		log.info("UserService::findAll:Entry");

		List<User> users = userRepository.findAll();

		// Convert each User entity to a DTO
		List<UserDto> result = new ArrayList<>();
		for (User u : users) {
			UserDto uDto = convertToDTO(u);
			result.add(uDto);
		}

		log.info("UserService::findAll:Exit successfully");
		return result;

	}

	// findById
	public UserDto findById(String userId) {
		log.info("UserService::findById:Entry for User ID: {}", userId);

		List<User> users = userRepository.findAll();

		for (User u : users) {
			if (u.getUserId().equals(userId)) {
				log.info("UserService::findById:User found with ID: {}", userId);
				return convertToDTO(u);
			}
		}

		log.warn("UserService::findById:No user found with ID: {}", userId);
		throw new ResourceNotFoundException("User not found with ID: " + userId);

	}

	// findUserByEmailAndPasswordhash
	// userLogin
	public String userLogin(String email, String password, Role role) {
	    log.info("UserService::userLogin: Entry for email: {}", email);

	    // Authenticate user credentials
	    Authentication authentication = authManager
	            .authenticate(new UsernamePasswordAuthenticationToken(email, password));

	    if (!authentication.isAuthenticated()) {
	        log.warn("UserService::userLogin: Invalid login credentials for email: {}", email);
	        throw new UnauthorizedAccessException("Invalid Login Credentials!");
	    }

	    // Find the authenticated user
	    User authUser = userRepository.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

	    // Compare role names, not object references
	    if (!authUser.getRole().getRoleName().equals(role.getRoleName())) {
	        log.warn("UserService::userLogin: Role mismatch for email: {}", email);
	        throw new UnauthorizedAccessException("Forbidden! You don't have access!");
	    }

	    // Generate and return JWT token
	    String token = jwtService.getToken(authUser.getUserId(), authUser.getEmail(), authUser.getRole().getRoleName());
	    log.info("UserService::userLogin: Token generated successfully for email: {}", email);

	    return token;
	}




	// addUser
	public UserDto saveUser(UserDto uDto) {
		log.info("UserService::saveUser:Entry with data: {}", uDto);

		if (!(roleRepository.existsById(uDto.getRoleId()))) {
			throw new ResourceNotFoundException(
					"Role with the entered ID is not found. Please check the Role ID again");
		}

		User u = convertToEntity(uDto);
		
	    u.setPasswordHash(passwordEncoder.encode(uDto.getPasswordHash()));
		User saved = userRepository.save(u);
		log.info("UserService::saveUser:User saved successfully with ID: {}", saved.getUserId());
		return convertToDTO(saved);

	}

	// updateUser
	public UserDto updateUser(String userId, UserDto uDto) {
		log.info("UserService::updateUser:Entry for User ID: {}", userId);

		Optional<User> optionalUser = userRepository.findById(userId);

		if (optionalUser.isPresent()) {
			User existingUser = optionalUser.get();

			 // Update other fields
	        existingUser.setEmail(uDto.getEmail());

	        // Encode the updated password
	        if (uDto.getPasswordHash() != null) { // Check if the password is being updated
	            existingUser.setPasswordHash(passwordEncoder.encode(uDto.getPasswordHash()));
	        }
			User saved = userRepository.save(existingUser);

			log.info("UserService::updateUser:User updated successfully for ID: {}", userId);
			return convertToDTO(saved);
		} else {
			log.warn("UserService::updateUser:User not found with ID: {}", userId);
			throw new ResourceNotFoundException("User not found with ID: " + userId);
		}

	}

	// deleteUserById
	public void deleteById(String userId) {
		log.info("UserService::deleteById:Entry for User ID: {}", userId);

		if (userRepository.existsById(userId)) {
			userRepository.deleteById(userId);
			log.info("UserService::deleteById:User deleted successfully for ID: {}", userId);
		} else {
			log.warn("UserService::deleteById:User not found with ID: {}", userId);
			throw new ResourceNotFoundException("User not found with ID: " + userId);
		}

	}

	// deleteAllUsers
	public void deleteAllUsers() {
		log.info("UserService::deleteAllUsers:Entry");

		userRepository.deleteAll();
		log.info("UserService::deleteAllUsers:All users deleted successfully");

	}

	// --------------------------------------------------------------------------------------------------------------------------

	// addPatient
	@Transactional
	public UserRegisterPatientDto registerPatient(UserRegisterPatientDto uRDto) {

		if (!(roleRepository.existsById(uRDto.getRoleId()))) {
			throw new ResourceNotFoundException("Role with the given ID does not exist. Enter valid Role ID");
		}

		if (userRepository.existsByEmail(uRDto.getEmail())) {
			throw new DuplicateEntryException("Duplicate Entry : User with this email already exists");
		}
		
		if(patientRepository.existsByContactDetails(uRDto.getContactDetails())) {
			throw new DuplicateEntryException("Duplicate Entry : User with this Phone number already exists");
		}

		// create and save user
		User u = new User();

		u.setCreatedAt(uRDto.getCreatedAt());
		u.setUpdatedAt(uRDto.getUpdatedAt());

		Role r = roleRepository.findById(uRDto.getRoleId()).orElse(null);
		u.setRole(r);

		u.setUserId(uRDto.getUserId());
		u.setEmail(uRDto.getEmail());
	    u.setPasswordHash(passwordEncoder.encode(uRDto.getPasswordHash()));
		User savedUser = userRepository.save(u);

		uRDto.setUserId(savedUser.getUserId());
		uRDto.setCreatedAt(savedUser.getCreatedAt());
		uRDto.setUpdatedAt(savedUser.getUpdatedAt());

		// create and save patient
		if (uRDto.getName() != null) {
			Patient p = new Patient();

			p.setCreatedAt(uRDto.getCreatedAt());
			p.setUpdatedAt(uRDto.getUpdatedAt());
			// p.setPatientId(savedUser.getUserId());
			p.setUser(savedUser);
			p.setName(uRDto.getName());
			p.setDateOfBirth(uRDto.getDateOfBirth());
			p.setContactDetails(uRDto.getContactDetails());
			p.setGender(uRDto.getGender());
			p.setHeightInCm(uRDto.getHeightInCm());
			p.setWeightInCm(uRDto.getWeightInKg());

			Patient savedPatient = patientRepository.save(p);

			uRDto.setPatientId(savedPatient.getPatientId());

		}

		return uRDto;

	}

	// addDoctor
	@Transactional
	public UserRegisterDoctorDto registerDoctor(UserRegisterDoctorDto uRDto) {

		if (!(roleRepository.existsById(uRDto.getRoleId()))) {
			throw new ResourceNotFoundException("Role with the given ID does not exist. Enter valid Role ID");
		}

		if (userRepository.existsByEmail(uRDto.getEmail())) {
			throw new DuplicateEntryException("Duplicate Entry : User with this email already exists");
		}

		// create and save user
		User u = new User();

		u.setCreatedAt(uRDto.getCreatedAt());
		u.setUpdatedAt(uRDto.getUpdatedAt());

		Role r = roleRepository.findById(uRDto.getRoleId()).orElse(null);
		u.setRole(r);

		u.setUserId(uRDto.getUserId());
		u.setEmail(uRDto.getEmail());
	    u.setPasswordHash(passwordEncoder.encode(uRDto.getPasswordHash()));
		User savedUser = userRepository.save(u);

		uRDto.setCreatedAt(savedUser.getCreatedAt());
		uRDto.setUpdatedAt(savedUser.getUpdatedAt());
		uRDto.setDoctorId(savedUser.getUserId());

		// create and save doctor
		try {

			UserRegisterDoctorDto newURDto = doctorClient.addDoctor(uRDto).getBody().getData();
			return newURDto;

		} catch (CustomException e) {
			throw new CustomException("Error calling DOCTOR-SERVICE: " + e.getMessage());
		}

	}

}
