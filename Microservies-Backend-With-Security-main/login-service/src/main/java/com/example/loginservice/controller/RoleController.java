package com.example.loginservice.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.loginservice.dto.RoleDto;
import com.example.loginservice.model.ResultResponse;
import com.example.loginservice.service.RoleService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/role")
@CrossOrigin(origins = "http://localhost:3000")
public class RoleController {

	@Autowired
	private RoleService roleService;

	// getAllRoles
	@GetMapping("/getAll")
	public ResponseEntity<ResultResponse<List<RoleDto>>> findAll() {
		log.info("RoleController::findAll:Entry");

		List<RoleDto> roles = roleService.findAll();

		ResultResponse<List<RoleDto>> resultResponse = ResultResponse.<List<RoleDto>>builder().success(true)
				.message("Roles retrieved successfully").data(roles).timestamp(LocalDateTime.now(ZoneOffset.UTC))
				.build();

		log.info("RoleController::findAll:Exit successfully. Retrieved {} roles.", roles.size());
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// getRoleById
	@GetMapping("/id/{roleId}")
	public ResponseEntity<ResultResponse<RoleDto>> findById(@Valid @PathVariable String roleId) {
		log.info("RoleController::findById:Entry for Role ID: {}", roleId);

		RoleDto role = roleService.findById(roleId);

		ResultResponse<RoleDto> resultResponse = ResultResponse.<RoleDto>builder().success(true)
				.message("Role retrieved successfully").data(role).timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("RoleController::findById:Exit successfully for Role ID: {}", roleId);
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// getRoleByName
	@GetMapping("/name/{roleName}")
	public ResponseEntity<ResultResponse<RoleDto>> findByName(@Valid @PathVariable String roleName) {
		log.info("RoleController::findByName:Entry for Role Name: {}", roleName);

		RoleDto role = roleService.findByName(roleName);

		ResultResponse<RoleDto> resultResponse = ResultResponse.<RoleDto>builder().success(true)
				.message("Role retrieved successfully").data(role).timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("RoleController::findByName:Exit successfully for Role Name: {}", roleName);
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// addRole
	@PostMapping("/add")
	public ResponseEntity<ResultResponse<RoleDto>> addRole(@Valid @RequestBody RoleDto rDto) {
		log.info("RoleController::addRole:Entry with data: {}", rDto);

		RoleDto savedRole = roleService.saveRole(rDto);

		ResultResponse<RoleDto> resultResponse = ResultResponse.<RoleDto>builder().success(true)
				.message("Role added successfully").data(savedRole).timestamp(LocalDateTime.now(ZoneOffset.UTC))
				.build();

		log.info("RoleController::addRole:Exit successfully with Role ID: {}", savedRole.getRoleId());
		return new ResponseEntity<>(resultResponse, HttpStatus.CREATED);
	}

	// updateRoleById
	@PutMapping("/update/{roleId}")
	public ResponseEntity<ResultResponse<RoleDto>> updateRole(@Valid @PathVariable String roleId,
			@Valid @RequestBody RoleDto rDto) {
		log.info("RoleController::updateRole:Entry for Role ID: {}", roleId);

		RoleDto updatedRole = roleService.updateRole(roleId, rDto);

		ResultResponse<RoleDto> resultResponse = ResultResponse.<RoleDto>builder().success(true)
				.message("Role updated successfully").data(updatedRole).timestamp(LocalDateTime.now(ZoneOffset.UTC))
				.build();

		log.info("RoleController::updateRole:Exit successfully for Role ID: {}", roleId);
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	// deleteRoleById
	@DeleteMapping("/delete/{roleId}")
	public ResponseEntity<ResultResponse<Void>> deleteById(@Valid @PathVariable String roleId) {
		log.info("RoleController::deleteById:Entry for Role ID: {}", roleId);

		roleService.deleteById(roleId);

		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("Role deleted successfully").data(null).timestamp(LocalDateTime.now(ZoneOffset.UTC)).build();

		log.info("RoleController::deleteById:Exit successfully for Role ID: {}", roleId);
		return new ResponseEntity<>(resultResponse, HttpStatus.NO_CONTENT);
	}

	// deleteAllRoles
	@DeleteMapping("/deleteAll")
	public ResponseEntity<ResultResponse<Void>> deleteAllRoles() {
		log.info("RoleController::deleteAllRoles:Entry");

		roleService.deleteAllRoles();

		ResultResponse<Void> resultResponse = ResultResponse.<Void>builder().success(true)
				.message("All roles deleted successfully").data(null).timestamp(LocalDateTime.now(ZoneOffset.UTC))
				.build();

		log.info("RoleController::deleteAllRoles:Exit successfully");
		return new ResponseEntity<>(resultResponse, HttpStatus.NO_CONTENT);
	}
}
