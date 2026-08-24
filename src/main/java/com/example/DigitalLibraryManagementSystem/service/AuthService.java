package com.example.DigitalLibraryManagementSystem.service;
import com.example.DigitalLibraryManagementSystem.Security.JwtService;
import com.example.DigitalLibraryManagementSystem.dto.AuthResponse;
import com.example.DigitalLibraryManagementSystem.dto.LoginRequest;
import com.example.DigitalLibraryManagementSystem.dto.RegisterRequest;
import com.example.DigitalLibraryManagementSystem.entity.Role;
import com.example.DigitalLibraryManagementSystem.entity.User;
import com.example.DigitalLibraryManagementSystem.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            return "Email already registered";
        }

        // FIXED: Added the missing 's' to match the updated repository method
        if(userRepository.existsByUsername(request.getUsername())) {
            return "UserName Already Exist";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.USER);
        userRepository.save(user);

        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                ()->new RuntimeException("User Not found"));
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(
                user.getId(),
                token,
                user.getUsername(),
                user.getRole().name()
        );
    }
    public String forgotPassword(String email) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "If the email exists, a password reset link has been sent.";
        }

        User user = optionalUser.get();

        SecureRandom secureRandom = new SecureRandom();

        byte[] randomBytes = new byte[32];

        secureRandom.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        user.setResetToken(token);

        user.setResetTokenExpiry(
                LocalDateTime.now().plusMinutes(15)
        );

        userRepository.save(user);

        String resetLink =
                "http://localhost:8081/HTML/reset-password.html?token="
                        + token;

        System.out.println("RESET LINK = " + resetLink);

        return resetLink;
    }
    public void resetPassword(
            String token,
            String newPassword) {

        User user = userRepository
                .findByResetToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid reset token")
                );

        if (user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry()
                        .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Reset token has expired"
            );
        }

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        user.setResetToken(null);

        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }

}
