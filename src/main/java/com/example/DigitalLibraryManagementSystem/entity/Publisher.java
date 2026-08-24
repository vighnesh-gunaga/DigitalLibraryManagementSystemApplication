package com.example.DigitalLibraryManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "publishers")
@Data
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}