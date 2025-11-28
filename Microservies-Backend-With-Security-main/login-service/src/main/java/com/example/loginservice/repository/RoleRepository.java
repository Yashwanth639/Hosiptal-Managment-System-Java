package com.example.loginservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.loginservice.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

	// findRoleByName
	@Query("SELECT r FROM Role r WHERE r.roleName = :roleName")
	Optional<Role> findByRoleName(String roleName);

}
