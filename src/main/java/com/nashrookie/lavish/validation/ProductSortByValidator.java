package com.nashrookie.lavish.validation;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductSortByValidator implements 
  ConstraintValidator<ProductSortByConstraint, String> {

    private static List<String> validSortBy = List.of("createdOn", "price", "quantity");

    @Override
    public void initialize(ProductSortByConstraint constraint) {
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
