package com.example.loginservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.loginservice.dto.RoleDto;

import com.example.loginservice.exception.ResourceNotFoundException;
import com.example.loginservice.model.Role;
import com.example.loginservice.repository.RoleRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleService {

	@Autowired
	private RoleRepository roleRepo;

	// convertEntityToDTO
	private RoleDto convertToDTO(Role r) {
		RoleDto rDto = new RoleDto();
		rDto.setCreatedAt(r.getCreatedAt());
		rDto.setUpdatedAt(r.getUpdatedAt());
		rDto.setRoleId(r.getRoleId());
		rDto.setRoleName(r.getRoleName());

		return rDto;
	}

	// convertDTOToEntity
	private Role convertToEntity(RoleDto rDto) {
		Role r = new Role();
		r.setCreatedAt(rDto.getCreatedAt());
		r.setUpdatedAt(rDto.getUpdatedAt());
		r.setRoleId(rDto.getRoleId());
		r.setRoleName(rDto.getRoleName());

		return r;
	}

	// findAllRoles
	public List<RoleDto> findAll() {
		log.info("RoleService::findAll:Entry");

		List<Role> roles = roleRepo.findAll();

		// Convert roles to DTOs
		List<RoleDto> result = new ArrayList<>();
		for (Role r : roles) {
			RoleDto rDto = convertToDTO(r);
			result.add(rDto);
		}

		log.info("RoleService::findAll:Exit successfully");
		return result;

	}

	// findRoleById
	public RoleDto findById(String roleId) {
		log.info("RoleService::findById:Entry for Role ID: {}", roleId);

		List<Role> roles = roleRepo.findAll();

		for (Role r : roles) {
			if (r.getRoleId().equals(roleId)) {
				log.info("RoleService::findById:Role found with ID: {}", roleId);
				return convertToDTO(r);
			}
		}

		log.warn("RoleService::findById:Role not found with ID: {}", roleId);
		throw new ResourceNotFoundException("Role not found with ID: " + roleId);

	}

	// findRoleByName
	public RoleDto findByName(String roleName) {
		log.info("RoleService::findByName:Entry for Role Name: {}", roleName);

		List<Role> roles = roleRepo.findAll();

		for (Role r : roles) {
			if (r.getRoleName().equals(roleName)) {
				log.info("RoleService::findByName:Role found with Name: {}", roleName);
				return convertToDTO(r);
			}
		}

		log.warn("RoleService::findByName:Role not found with Name: {}", roleName);
		throw new ResourceNotFoundException("Role not found with Name: " + roleName);

	}

	// addRole
	public RoleDto saveRole(RoleDto rDto) {
		log.info("RoleService::saveRole:Entry with data: {}", rDto);

		Role r = convertToEntity(rDto);
		Role saved = roleRepo.save(r);

		log.info("RoleService::saveRole:Role saved successfully with ID: {}", saved.getRoleId());
		return convertToDTO(saved);

	}

	// updateRole
	public RoleDto updateRole(String roleId, RoleDto rDto) {
		log.info("RoleService::updateRole:Entry for Role ID: {}", roleId);

		Optional<Role> optionalRole = roleRepo.findById(roleId);

		if (optionalRole.isPresent()) {
			Role existingRole = optionalRole.get();

			// Update the role
			existingRole.setRoleName(rDto.getRoleName());
			Role saved = roleRepo.save(existingRole);

			log.info("RoleService::updateRole:Role updated successfully for ID: {}", roleId);
			return convertToDTO(saved);

		} else {
			log.warn("RoleService::updateRole:Role not found with ID: {}", roleId);
			throw new ResourceNotFoundException("Role not found with ID: " + roleId);
		}

	}

	// deleteRoleById
	public void deleteById(String roleId) {
		log.info("RoleService::deleteById:Entry for Role ID: {}", roleId);

		if (roleRepo.existsById(roleId)) {
			roleRepo.deleteById(roleId);
			log.info("RoleService::deleteById:Role deleted successfully for ID: {}", roleId);
		} else {
			log.warn("RoleService::deleteById:Role not found with ID: {}", roleId);
			throw new ResourceNotFoundException("Role not found with ID: " + roleId);
		}
	}

	// deleteAllRoles
	public void deleteAllRoles() {
		log.info("RoleService::deleteAllRoles:Entry");

		roleRepo.deleteAll();
		log.info("RoleService::deleteAllRoles:All roles deleted successfully");

	}

	// ----------------------------------------------------------------------------------------------------------------------------

	// validation
	// findAllRoleNames
	public List<String> findAllRoleNames() {
		List<Role> roles = roleRepo.findAll();
		List<String> roleNames = new ArrayList<String>();

		for (Role role : roles) {
			roleNames.add(role.getRoleName());
		}
		return roleNames;
	}

}
