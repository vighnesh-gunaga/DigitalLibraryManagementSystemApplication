package com.example.DigitalLibraryManagementSystem.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FineResponse {

    private Long id;

    private Long issuedBookId;

    private Long userId;

    private String username;

    private Long bookId;

    private String bookTitle;

    private LocalDate dueDate;

    private LocalDate returnDate;

    private long overdueDays;

    private double amount;

    private boolean paid;

    private LocalDate createdAt;
}