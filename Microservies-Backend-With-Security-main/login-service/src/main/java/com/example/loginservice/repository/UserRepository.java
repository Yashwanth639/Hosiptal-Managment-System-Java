package com.example.loginservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.loginservice.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	// findUserByEmail
	@Query("SELECT u FROM User u WHERE u.email = :email")
	Optional<User> findByEmail(@Param("email") String email);

	// checkIfUserAlreadyExistsByEmail
	boolean existsByEmail(String email);
}
