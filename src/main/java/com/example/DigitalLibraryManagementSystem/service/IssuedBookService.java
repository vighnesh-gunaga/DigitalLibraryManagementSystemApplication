package com.example.DigitalLibraryManagementSystem.service;

import com.example.DigitalLibraryManagementSystem.dto.IssueBookRequest;
import com.example.DigitalLibraryManagementSystem.dto.IssuedBookResponse;
import com.example.DigitalLibraryManagementSystem.entity.Book;
import com.example.DigitalLibraryManagementSystem.entity.IssueStatus;
import com.example.DigitalLibraryManagementSystem.entity.IssuedBook;
import com.example.DigitalLibraryManagementSystem.entity.User;
import com.example.DigitalLibraryManagementSystem.repository.BookRepository;
import com.example.DigitalLibraryManagementSystem.repository.IssuedBookRepository;
import com.example.DigitalLibraryManagementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class IssuedBookService {

    private final IssuedBookRepository issuedBookRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public IssuedBookService(
            IssuedBookRepository issuedBookRepository,
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.issuedBookRepository = issuedBookRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // Issue Book
    @Transactional
    public IssuedBookResponse issueBook(IssueBookRequest request) {

        // Find user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found with ID: "
                                + request.getUserId()));

        // Find book
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new RuntimeException("Book not found with ID: "
                                + request.getBookId()));

        // Check available quantity
        if (book.getAvailableQuantity() <= 0) {
            throw new RuntimeException("Book is currently not available");
        }

        // Check whether user already has this book
        issuedBookRepository
                .findByUserIdAndBookIdAndStatus(
                        request.getUserId(),
                        request.getBookId(),
                        IssueStatus.ISSUED
                )
                .ifPresent(existing -> {
                    throw new RuntimeException(
                            "User already has this book issued"
                    );
                });

        // Create issued book
        IssuedBook issuedBook = new IssuedBook();

        issuedBook.setUser(user);
        issuedBook.setBook(book);

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(7);

        issuedBook.setIssueDate(issueDate);
        issuedBook.setDueDate(dueDate);
        issuedBook.setStatus(IssueStatus.ISSUED);

        // Decrease available quantity
        book.setAvailableQuantity(
                book.getAvailableQuantity() - 1
        );

        bookRepository.save(book);

        IssuedBook savedIssuedBook =
                issuedBookRepository.save(issuedBook);

        return convertToResponse(savedIssuedBook);
    }


    // Get all issued books
    public List<IssuedBookResponse> getAllIssuedBooks() {

        return issuedBookRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Get issued books by user
    public List<IssuedBookResponse> getIssuedBooksByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException(
                    "User not found with ID: " + userId
            );
        }

        return issuedBookRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Get issued book by ID
    public IssuedBookResponse getIssuedBookById(Long id) {

        IssuedBook issuedBook =
                issuedBookRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Issued book not found with ID: " + id
                                ));

        return convertToResponse(issuedBook);
    }


    // Return Book
    @Transactional
    public IssuedBookResponse returnBook(Long id) {

        IssuedBook issuedBook =
                issuedBookRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Issued book not found with ID: " + id
                                ));

        // Check already returned
        if (issuedBook.getStatus() == IssueStatus.RETURNED) {
            throw new RuntimeException(
                    "Book has already been returned"
            );
        }

        Book book = issuedBook.getBook();

        // Increase available quantity
        book.setAvailableQuantity(
                book.getAvailableQuantity() + 1
        );

        bookRepository.save(book);

        // Set return details
        issuedBook.setReturnDate(LocalDate.now());
        issuedBook.setStatus(IssueStatus.RETURNED);

        IssuedBook updated =
                issuedBookRepository.save(issuedBook);

        return convertToResponse(updated);
    }


    // Convert Entity → Response DTO
    private IssuedBookResponse convertToResponse(
            IssuedBook issuedBook
    ) {

        IssuedBookResponse response =
                new IssuedBookResponse();

        response.setId(issuedBook.getId());

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

        response.setIssueDate(
                issuedBook.getIssueDate()
        );

        response.setDueDate(
                issuedBook.getDueDate()
        );

        response.setReturnDate(
                issuedBook.getReturnDate()
        );

        response.setStatus(
                issuedBook.getStatus().name()
        );

        return response;
    }
}