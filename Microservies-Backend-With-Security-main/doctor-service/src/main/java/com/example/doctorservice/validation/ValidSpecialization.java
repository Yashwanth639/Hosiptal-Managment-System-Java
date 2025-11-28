package com.example.doctorservice.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = SpecializationValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSpecialization {

	String message() default "Invalid specialization: 1. Specialization is either duplicate or improperly formatted";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}