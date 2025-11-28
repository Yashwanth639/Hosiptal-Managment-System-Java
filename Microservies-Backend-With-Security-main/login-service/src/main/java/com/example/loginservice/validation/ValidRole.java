package com.example.loginservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Constraint(validatedBy = RoleValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRole {

	String message() default "Invalid role: 1. Role is either duplicate or improperly formatted";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
