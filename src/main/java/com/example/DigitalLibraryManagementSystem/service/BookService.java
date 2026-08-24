package com.example.DigitalLibraryManagementSystem.service;

import com.example.DigitalLibraryManagementSystem.dto.BookRequest;
import com.example.DigitalLibraryManagementSystem.dto.BookResponse;
import com.example.DigitalLibraryManagementSystem.entity.Book;
import com.example.DigitalLibraryManagementSystem.entity.Category;
import com.example.DigitalLibraryManagementSystem.entity.Publisher;
import com.example.DigitalLibraryManagementSystem.repository.BookRepository;
import com.example.DigitalLibraryManagementSystem.repository.CategoryRepository;
import com.example.DigitalLibraryManagementSystem.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;


    // Add Book
    public BookResponse addBook(BookRequest request) {

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new RuntimeException("Book with this ISBN already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() ->
                        new RuntimeException("Publisher not found"));

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Book book = new Book();

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setQuantity(request.getQuantity());

        // Initially all copies are available
        book.setAvailableQuantity(request.getQuantity());

        Book savedBook = bookRepository.save(book);

        return convertToResponse(savedBook);
    }


    // Get All Books
    public List<BookResponse> getAllBooks() {

        return bookRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Get Book By ID
    public BookResponse getBookById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Book not found"));

        return convertToResponse(book);
    }


    // Update Book
    public BookResponse updateBook(Long id, BookRequest request) {

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Book not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() ->
                        new RuntimeException("Publisher not found"));


        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setCategory(category);
        existingBook.setPublisher(publisher);

        /*
         * Don't blindly replace availableQuantity when updating
         * the total quantity because some copies may already be issued.
         *
         * Example:
         * Total quantity = 5
         * Available = 3
         * Two books are currently issued.
         *
         * If quantity becomes 7:
         * Available should become 5.
         */

        int issuedBooks =
                existingBook.getQuantity()
                        - existingBook.getAvailableQuantity();

        if (request.getQuantity() < issuedBooks) {
            throw new RuntimeException(
                    "Quantity cannot be less than currently issued books");
        }

        existingBook.setQuantity(request.getQuantity());

        existingBook.setAvailableQuantity(
                request.getQuantity() - issuedBooks
        );

        Book updatedBook = bookRepository.save(existingBook);

        return convertToResponse(updatedBook);
    }


    // Delete Book
    public void deleteBook(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Book not found"));

        bookRepository.delete(book);
    }


    // Search By Title
    public List<BookResponse> searchByTitle(String title) {

        return bookRepository
                .findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Search By Author
    public List<BookResponse> searchByAuthor(String author) {

        return bookRepository
                .findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Search By Category
    public List<BookResponse> searchByCategory(String category) {

        return bookRepository
                .findByCategoryNameIgnoreCase(category)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Convert Book Entity → BookResponse
    private BookResponse convertToResponse(Book book) {

        BookResponse response = new BookResponse();

        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());

        response.setCategoryId(
                book.getCategory().getId()
        );

        response.setCategoryName(
                book.getCategory().getName()
        );

        response.setPublisherId(
                book.getPublisher().getId()
        );

        response.setPublisherName(
                book.getPublisher().getName()
        );

        response.setQuantity(
                book.getQuantity()
        );

        response.setAvailableQuantity(
                book.getAvailableQuantity()
        );

        return response;
    }
}