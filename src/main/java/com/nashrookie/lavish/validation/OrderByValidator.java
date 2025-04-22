package com.nashrookie.lavish.validation;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OrderByValidator implements 
  ConstraintValidator<OrderByConstraint, String> {

    private static List<String> validOrderBy = List.of("createdOn", "price");

    @Override
    public void initialize(OrderByConstraint constraint) {
    }

    @Override
    public boolean isValid(String orderByField,
      ConstraintValidatorContext cxt) {
        if (orderByField == null) {
            return false;
        }
        return validOrderBy.contains(orderByField);
    }
}
