package com.example.DigitalLibraryManagementSystem.repository;

import com.example.DigitalLibraryManagementSystem.entity.IssueStatus;
import com.example.DigitalLibraryManagementSystem.entity.IssuedBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssuedBookRepository extends JpaRepository<IssuedBook, Long> {

    List<IssuedBook> findByUserId(Long userId);

    List<IssuedBook> findByBookId(Long bookId);

    List<IssuedBook> findByStatus(IssueStatus status);

    Optional<IssuedBook> findByUserIdAndBookIdAndStatus(
            Long userId,
            Long bookId,
            IssueStatus status
    );
}