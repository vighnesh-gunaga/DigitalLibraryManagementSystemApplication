package com.example.DigitalLibraryManagementSystem.dto;

import lombok.Data;

@Data
public class BookRequest {

    private String title;
    private String author;
    private String  isbn;
    private Long categoryId;
    private Long publisherId;
    private int quantity;
}
