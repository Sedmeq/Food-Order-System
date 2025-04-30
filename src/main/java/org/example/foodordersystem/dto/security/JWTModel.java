package org.example.foodordersystem.dto.security;

import lombok.Data;

import java.util.List;

@Data
public class JWTModel {
    private List<String> roles;
    private Long userId;
}
