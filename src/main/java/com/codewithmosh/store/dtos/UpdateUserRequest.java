package com.codewithmosh.store.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;

}
