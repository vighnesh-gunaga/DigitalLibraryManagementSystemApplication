package com.example.DigitalLibraryManagementSystem.dto;

import lombok.Data;

@Data
public class BookResponse {

    private Long id;

    private String title;

    private String author;

    private String isbn;

    private Long categoryId;

    private String categoryName;

    private Long publisherId;

    private String publisherName;

    private int quantity;

    private int availableQuantity;
}