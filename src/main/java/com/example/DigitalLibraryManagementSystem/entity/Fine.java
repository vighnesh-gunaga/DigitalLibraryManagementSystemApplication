package com.example.DigitalLibraryManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "fines")
@Data
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "issued_book_id", nullable = false, unique = true)
    private IssuedBook issuedBook;

    private long overdueDays;

    private double amount;

    private boolean paid;

    private LocalDate createdAt;
}