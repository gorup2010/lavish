package com.nashrookie.lavish.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = CategorySortByValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CategorySortByConstraint {
    String message() default "Invalid sortBy field in category filter. Allowed values are 'name' or 'createdOn'.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}