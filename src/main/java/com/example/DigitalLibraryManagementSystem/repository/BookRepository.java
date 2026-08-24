package com.example.DigitalLibraryManagementSystem.repository;

import com.example.DigitalLibraryManagementSystem.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByCategoryNameIgnoreCase(String category);

    boolean existsByIsbn(String isbn);
}