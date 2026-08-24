package com.example.DigitalLibraryManagementSystem.service;

import com.example.DigitalLibraryManagementSystem.dto.FineResponse;
import com.example.DigitalLibraryManagementSystem.entity.Fine;
import com.example.DigitalLibraryManagementSystem.entity.IssuedBook;
import com.example.DigitalLibraryManagementSystem.entity.IssueStatus;
import com.example.DigitalLibraryManagementSystem.repository.FineRepository;
import com.example.DigitalLibraryManagementSystem.repository.IssuedBookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FineService {

    private static final double FINE_PER_DAY = 5.0;

    private final FineRepository fineRepository;
    private final IssuedBookRepository issuedBookRepository;

    public FineService(
            FineRepository fineRepository,
            IssuedBookRepository issuedBookRepository
    ) {
        this.fineRepository = fineRepository;
        this.issuedBookRepository = issuedBookRepository;
    }


    // Calculate fine for an issued book
    @Transactional
    public FineResponse calculateFine(Long issuedBookId) {

        IssuedBook issuedBook = issuedBookRepository.findById(issuedBookId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issued book not found with ID: " + issuedBookId
                        )
                );

        // Book must be returned before calculating final fine
        if (issuedBook.getReturnDate() == null) {
            throw new RuntimeException(
                    "Book has not been returned yet"
            );
        }

        LocalDate dueDate = issuedBook.getDueDate();
        LocalDate returnDate = issuedBook.getReturnDate();

        long overdueDays = 0;

        if (returnDate.isAfter(dueDate)) {
            overdueDays = ChronoUnit.DAYS.between(
                    dueDate,
                    returnDate
            );
        }

        double amount = overdueDays * FINE_PER_DAY;

        // Check if fine already exists
        Fine fine = fineRepository
                .findByIssuedBookId(issuedBookId)
                .orElse(null);

        if (fine == null) {

            fine = new Fine();

            fine.setIssuedBook(issuedBook);
            fine.setOverdueDays(overdueDays);
            fine.setAmount(amount);
            fine.setPaid(false);
            fine.setCreatedAt(LocalDate.now());

        } else {

            // Update existing fine
            fine.setOverdueDays(overdueDays);
            fine.setAmount(amount);
        }

        Fine savedFine = fineRepository.save(fine);

        return convertToResponse(savedFine);
    }


    // Get all fines
    public List<FineResponse> getAllFines() {

        return fineRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Get fine by ID
    public FineResponse getFineById(Long id) {

        Fine fine = fineRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Fine not found with ID: " + id
                        )
                );

        return convertToResponse(fine);
    }


    // Get fines by user
    public List<FineResponse> getFinesByUser(Long userId) {

        return fineRepository.findByIssuedBookUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Get unpaid fines
    public List<FineResponse> getUnpaidFines() {

        return fineRepository.findByPaidFalse()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Mark fine as paid
    @Transactional
    public FineResponse payFine(Long id) {

        Fine fine = fineRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Fine not found with ID: " + id
                        )
                );

        if (fine.isPaid()) {
            throw new RuntimeException(
                    "Fine has already been paid"
            );
        }

        fine.setPaid(true);

        Fine updatedFine = fineRepository.save(fine);

        return convertToResponse(updatedFine);
    }


    // Convert Entity → Response DTO
    private FineResponse convertToResponse(Fine fine) {

        FineResponse response = new FineResponse();

        IssuedBook issuedBook = fine.getIssuedBook();

        response.setId(fine.getId());

        response.setIssuedBookId(
                issuedBook.getId()
        );

        response.setUserId(
                issuedBook.getUser().getId()
        );

        response.setUsername(
                issuedBook.getUser().getUsername()
        );

        response.setBookId(
                issuedBook.getBook().getId()
        );

        response.setBookTitle(
                issuedBook.getBook().getTitle()
        );

        response.setDueDate(
                issuedBook.getDueDate()
        );

        response.setReturnDate(
                issuedBook.getReturnDate()
        );

        response.setOverdueDays(
                fine.getOverdueDays()
        );

        response.setAmount(
                fine.getAmount()
        );

        response.setPaid(
                fine.isPaid()
        );

        response.setCreatedAt(
                fine.getCreatedAt()
        );

        return response;
    }
}