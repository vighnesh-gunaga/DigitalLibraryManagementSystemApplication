package com.example.DigitalLibraryManagementSystem.service;

import com.example.DigitalLibraryManagementSystem.dto.ReservationRequest;
import com.example.DigitalLibraryManagementSystem.dto.ReservationResponse;
import com.example.DigitalLibraryManagementSystem.entity.Book;
import com.example.DigitalLibraryManagementSystem.entity.Reservation;
import com.example.DigitalLibraryManagementSystem.entity.ReservationStatus;
import com.example.DigitalLibraryManagementSystem.entity.User;
import com.example.DigitalLibraryManagementSystem.repository.BookRepository;
import com.example.DigitalLibraryManagementSystem.repository.ReservationRepository;
import com.example.DigitalLibraryManagementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }


    // Create reservation
    @Transactional
    public ReservationResponse createReservation(
            ReservationRequest request
    ) {

        // Find user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with ID: "
                                        + request.getUserId()
                        )
                );

        // Find book
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found with ID: "
                                        + request.getBookId()
                        )
                );

        // Book is available
        if (book.getAvailableQuantity() > 0) {

            throw new RuntimeException(
                    "Book is currently available. You can issue the book directly."
            );
        }

        // Check existing reservation
        reservationRepository
                .findByUserIdAndBookIdAndStatus(
                        request.getUserId(),
                        request.getBookId(),
                        ReservationStatus.PENDING
                )
                .ifPresent(existing -> {

                    throw new RuntimeException(
                            "You already have a reservation for this book."
                    );
                });

        // Create reservation
        Reservation reservation = new Reservation();

        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation =
                reservationRepository.save(reservation);

        return convertToResponse(savedReservation);
    }


    // Get all reservations
    public List<ReservationResponse> getAllReservations() {

        return reservationRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Get reservation by ID
    public ReservationResponse getReservationById(Long id) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found with ID: "
                                                + id
                                )
                        );

        return convertToResponse(reservation);
    }


    // Get reservations by user
    public List<ReservationResponse> getReservationsByUser(
            Long userId
    ) {

        if (!userRepository.existsById(userId)) {

            throw new RuntimeException(
                    "User not found with ID: " + userId
            );
        }

        return reservationRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Get reservations by book
    public List<ReservationResponse> getReservationsByBook(
            Long bookId
    ) {

        if (!bookRepository.existsById(bookId)) {

            throw new RuntimeException(
                    "Book not found with ID: " + bookId
            );
        }

        return reservationRepository.findByBookId(bookId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Get pending reservations
    public List<ReservationResponse> getPendingReservations() {

        return reservationRepository
                .findByStatus(ReservationStatus.PENDING)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // Cancel reservation
    @Transactional
    public ReservationResponse cancelReservation(Long id) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found with ID: "
                                                + id
                                )
                        );

        if (reservation.getStatus()
                != ReservationStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending reservations can be cancelled."
            );
        }

        reservation.setStatus(
                ReservationStatus.CANCELLED
        );

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        return convertToResponse(updatedReservation);
    }


    // Fulfill reservation
    @Transactional
    public ReservationResponse fulfillReservation(Long id) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found with ID: "
                                                + id
                                )
                        );

        if (reservation.getStatus()
                != ReservationStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending reservations can be fulfilled."
            );
        }

        reservation.setStatus(
                ReservationStatus.FULFILLED
        );

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        return convertToResponse(updatedReservation);
    }


    // Convert Entity → Response DTO
    private ReservationResponse convertToResponse(
            Reservation reservation
    ) {

        ReservationResponse response =
                new ReservationResponse();

        response.setId(
                reservation.getId()
        );

        response.setUserId(
                reservation.getUser().getId()
        );

        response.setUsername(
                reservation.getUser().getUsername()
        );

        response.setBookId(
                reservation.getBook().getId()
        );

        response.setBookTitle(
                reservation.getBook().getTitle()
        );

        response.setReservationDate(
                reservation.getReservationDate()
        );

        response.setStatus(
                reservation.getStatus().name()
        );

        return response;
    }
}