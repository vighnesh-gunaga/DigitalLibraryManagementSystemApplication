package com.example.DigitalLibraryManagementSystem.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationResponse {

    private Long id;

    private Long userId;

    private String username;

    private Long bookId;

    private String bookTitle;

    private LocalDate reservationDate;

    private String status;
}