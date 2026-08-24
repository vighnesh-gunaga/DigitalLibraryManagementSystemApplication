package com.example.DigitalLibraryManagementSystem.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String token;

    private String newPassword;
}
