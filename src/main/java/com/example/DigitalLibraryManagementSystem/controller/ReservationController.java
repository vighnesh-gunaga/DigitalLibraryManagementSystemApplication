package com.example.DigitalLibraryManagementSystem.controller;

import com.example.DigitalLibraryManagementSystem.dto.ReservationRequest;
import com.example.DigitalLibraryManagementSystem.dto.ReservationResponse;
import com.example.DigitalLibraryManagementSystem.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }


    // Create reservation
    @PostMapping("/create")
    public ReservationResponse createReservation(
            @RequestBody ReservationRequest request
    ) {

        return reservationService.createReservation(request);
    }


    // Get all reservations
    @GetMapping("/all")
    public List<ReservationResponse> getAllReservations() {

        return reservationService.getAllReservations();
    }


    // Get reservation by ID
    @GetMapping("/{id}")
    public ReservationResponse getReservationById(
            @PathVariable Long id
    ) {

        return reservationService.getReservationById(id);
    }


    // Get reservations by user
    @GetMapping("/user/{userId}")
    public List<ReservationResponse> getReservationsByUser(
            @PathVariable Long userId
    ) {

        return reservationService
                .getReservationsByUser(userId);
    }


    // Get reservations by book
    @GetMapping("/book/{bookId}")
    public List<ReservationResponse> getReservationsByBook(
            @PathVariable Long bookId
    ) {

        return reservationService
                .getReservationsByBook(bookId);
    }


    // Get pending reservations
    @GetMapping("/pending")
    public List<ReservationResponse> getPendingReservations() {

        return reservationService
                .getPendingReservations();
    }


    // Cancel reservation
    @PutMapping("/cancel/{id}")
    public ReservationResponse cancelReservation(
            @PathVariable Long id
    ) {

        return reservationService
                .cancelReservation(id);
    }


    // Fulfill reservation
    @PutMapping("/fulfill/{id}")
    public ReservationResponse fulfillReservation(
            @PathVariable Long id
    ) {

        return reservationService
                .fulfillReservation(id);
    }
}