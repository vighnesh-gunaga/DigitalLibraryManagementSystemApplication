package com.example.DigitalLibraryManagementSystem.dto;

import lombok.Data;

@Data
public class ReservationRequest {

    private Long userId;

    private Long bookId;
}