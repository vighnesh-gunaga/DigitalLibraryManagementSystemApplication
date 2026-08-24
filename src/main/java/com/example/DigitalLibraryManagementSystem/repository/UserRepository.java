package com.example.DigitalLibraryManagementSystem.repository;

import com.example.DigitalLibraryManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    // FIXED: Added the 's' to make it 'existsBy'
    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
}
