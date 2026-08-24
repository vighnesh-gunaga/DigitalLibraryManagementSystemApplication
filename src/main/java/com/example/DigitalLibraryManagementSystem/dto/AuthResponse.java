package com.example.DigitalLibraryManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private Long userId;

    private String token;

    private String username;

    private String role;
}