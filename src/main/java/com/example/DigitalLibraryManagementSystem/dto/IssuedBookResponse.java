package com.example.DigitalLibraryManagementSystem.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class IssuedBookResponse {

    private Long id;

    private Long userId;

    private String username;

    private Long bookId;

    private String bookTitle;

    private LocalDate issueDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    private String status;
}