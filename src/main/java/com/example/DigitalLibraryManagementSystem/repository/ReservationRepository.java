package com.example.DigitalLibraryManagementSystem.repository;

import com.example.DigitalLibraryManagementSystem.entity.Reservation;
import com.example.DigitalLibraryManagementSystem.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByBookId(Long bookId);

    List<Reservation> findByStatus(ReservationStatus status);

    Optional<Reservation> findByUserIdAndBookIdAndStatus(
            Long userId,
            Long bookId,
            ReservationStatus status
    );
}