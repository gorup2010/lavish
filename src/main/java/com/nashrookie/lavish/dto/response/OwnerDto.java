package com.nashrookie.lavish.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OwnerDto {
    private Long id;
    private String firstname;
    private String lastname;
}