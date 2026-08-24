package com.example.DigitalLibraryManagementSystem.controller;

import com.example.DigitalLibraryManagementSystem.dto.FineResponse;
import com.example.DigitalLibraryManagementSystem.service.FineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }


    // Calculate fine
    @PostMapping("/calculate/{issuedBookId}")
    public FineResponse calculateFine(
            @PathVariable Long issuedBookId
    ) {

        return fineService.calculateFine(issuedBookId);
    }


    // Get all fines
    @GetMapping("/all")
    public List<FineResponse> getAllFines() {

        return fineService.getAllFines();
    }


    // Get fine by ID
    @GetMapping("/{id}")
    public FineResponse getFineById(
            @PathVariable Long id
    ) {

        return fineService.getFineById(id);
    }


    // Get fines by user
    @GetMapping("/user/{userId}")
    public List<FineResponse> getFinesByUser(
            @PathVariable Long userId
    ) {

        return fineService.getFinesByUser(userId);
    }


    // Get unpaid fines
    @GetMapping("/unpaid")
    public List<FineResponse> getUnpaidFines() {

        return fineService.getUnpaidFines();
    }


    // Pay fine
    @PutMapping("/pay/{id}")
    public FineResponse payFine(
            @PathVariable Long id
    ) {

        return fineService.payFine(id);
    }
}