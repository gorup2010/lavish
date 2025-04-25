package com.nashrookie.lavish.validation;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SortByValidator implements 
  ConstraintValidator<SortByConstraint, String> {

    private static List<String> validSortBy = List.of("createdOn", "price", "quantity");

    @Override
    public void initialize(SortByConstraint constraint) {
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
