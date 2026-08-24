package com.example.DigitalLibraryManagementSystem.repository;

import com.example.DigitalLibraryManagementSystem.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher,Long> {
    boolean existsByNameIgnoreCase(String name);
}
