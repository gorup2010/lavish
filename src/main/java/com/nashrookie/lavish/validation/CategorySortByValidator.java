package com.nashrookie.lavish.validation;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CategorySortByValidator implements
        ConstraintValidator<CategorySortByConstraint, String> {

    private static final List<String> validSortBy = List.of("name", "createdOn");

    @Override
    public void initialize(CategorySortByConstraint constraint) {
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
