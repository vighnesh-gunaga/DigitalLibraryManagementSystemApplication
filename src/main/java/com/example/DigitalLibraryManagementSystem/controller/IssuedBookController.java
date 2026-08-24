package com.example.DigitalLibraryManagementSystem.controller;

import com.example.DigitalLibraryManagementSystem.dto.IssueBookRequest;
import com.example.DigitalLibraryManagementSystem.dto.IssuedBookResponse;
import com.example.DigitalLibraryManagementSystem.service.IssuedBookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issued-books")
public class IssuedBookController {

    private final IssuedBookService issuedBookService;

    public IssuedBookController(
            IssuedBookService issuedBookService
    ) {
        this.issuedBookService = issuedBookService;
    }


    // Issue book
    @PostMapping("/issue")
    public IssuedBookResponse issueBook(
            @RequestBody IssueBookRequest request
    ) {

        return issuedBookService.issueBook(request);
    }


    // Get all issued books
    @GetMapping("/all")
    public List<IssuedBookResponse> getAllIssuedBooks() {

        return issuedBookService.getAllIssuedBooks();
    }


    // Get issued books by user
    @GetMapping("/user/{userId}")
    public List<IssuedBookResponse> getIssuedBooksByUser(
            @PathVariable Long userId
    ) {

        return issuedBookService.getIssuedBooksByUser(userId);
    }


    // Get issued book by ID
    @GetMapping("/{id}")
    public IssuedBookResponse getIssuedBookById(
            @PathVariable Long id
    ) {

        return issuedBookService.getIssuedBookById(id);
    }


    // Return book
    @PutMapping("/return/{id}")
    public IssuedBookResponse returnBook(
            @PathVariable Long id
    ) {

        return issuedBookService.returnBook(id);
    }
}