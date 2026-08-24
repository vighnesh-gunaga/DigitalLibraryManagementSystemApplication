package com.example.DigitalLibraryManagementSystem.controller;

import com.example.DigitalLibraryManagementSystem.dto.BookRequest;
import com.example.DigitalLibraryManagementSystem.dto.BookResponse;
import com.example.DigitalLibraryManagementSystem.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Add new book
    @PostMapping("/addbook")
    public BookResponse addBook(@RequestBody BookRequest bookRequest) {
        return bookService.addBook(bookRequest);
    }

    // Get all books
    @GetMapping("/allbooks")
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    // Get book by ID
    @GetMapping("/book/{id}")
    public BookResponse getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    // Update book
    @PutMapping("/updatebook/{id}")
    public BookResponse updateBook(
            @PathVariable Long id,
            @RequestBody BookRequest bookRequest) {

        return bookService.updateBook(id, bookRequest);
    }

    // Delete book
    @DeleteMapping("/deletebook/{id}")
    public String deleteBook(@PathVariable Long id) {

        bookService.deleteBook(id);

        return "Book deleted successfully";
    }

    // Search by title
    @GetMapping("/search/title")
    public List<BookResponse> searchByTitle(
            @RequestParam String title) {

        return bookService.searchByTitle(title);
    }

    // Search by author
    @GetMapping("/search/author")
    public List<BookResponse> searchByAuthor(
            @RequestParam String author) {

        return bookService.searchByAuthor(author);
    }

    // Search by category
    @GetMapping("/search/category")
    public List<BookResponse> searchByCategory(
            @RequestParam String category) {

        return bookService.searchByCategory(category);
    }
}