package com.example.DigitalLibraryManagementSystem.repository;

import com.example.DigitalLibraryManagementSystem.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {

    Optional<Fine> findByIssuedBookId(Long issuedBookId);

    List<Fine> findByIssuedBookUserId(Long userId);

    List<Fine> findByPaidFalse();
}