package com.nashrookie.lavish.validation;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserSortByValidator implements
        ConstraintValidator<UserSortByConstraint, String> {

    private static final List<String> validSortBy = List.of("username", "createdOn");

    @Override
    public void initialize(UserSortByConstraint constraint) {
    }

    @Override
    public boolean isValid(String sortByField,
                           ConstraintValidatorContext cxt) {
        if (sortByField == null) {
            return false;
        }
        return validSortBy.contains(sortByField);
    }
}
