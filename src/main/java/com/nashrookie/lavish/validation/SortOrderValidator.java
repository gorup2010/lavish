package com.nashrookie.lavish.validation;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SortOrderValidator implements 
  ConstraintValidator<SortOrderConstraint, String> {

    private static List<String> validSortOrder = List.of("asc", "desc");

    @Override
    public void initialize(SortOrderConstraint constraint) {
    }

    @Override
    public boolean isValid(String sortOrderField,
      ConstraintValidatorContext cxt) {
        if (sortOrderField == null) {
            return false;
        }
        return validSortOrder.contains(sortOrderField);
    }
}
