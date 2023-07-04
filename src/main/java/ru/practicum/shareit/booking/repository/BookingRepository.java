package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByEndDesc(Long userId, Pageable pageable);

    List<Booking> findByBooker_IdAndStatusOrderByEndDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByEndDesc(Long userId, LocalDateTime current, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsAfterOrderByEndDesc(Long userId, LocalDateTime current, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
            Long userId,
            LocalDateTime current,
            LocalDateTime current1,
            Pageable pageable
    );

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :userId " +
            "ORDER BY b.start DESC"
    )
    List<Booking> findAllByOwner(Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :userId " +
            "AND b.start < :currentTime " +
            "AND b.end > :currentTime " +
            "ORDER BY b.start DESC"
    )
    List<Booking> findAllByOwnerCurrent(Long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :userId " +
            "AND b.end < :currentTime " +
            "ORDER BY b.start DESC"
    )
    List<Booking> findAllByOwnerPast(Long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :userId " +
            "AND b.start > :currentTime " +
            "ORDER BY b.start DESC"
    )
    List<Booking> findAllByOwnerFuture(Long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :userId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC"
    )
    List<Booking> findAllByOwnerAndStatus(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findFirst1ByItem_IdAndStartLessThanAndStatusOrderByEndDesc(
            Long itemId,
            LocalDateTime currentTime,
            BookingStatus status
    );

    List<Booking> findFirst1ByItem_IdAndStartGreaterThanAndStatusOrderByStartAsc(
            Long itemId,
            LocalDateTime currentTime,
            BookingStatus status
    );

    List<Booking> findFirst1ByItem_IdAndBooker_IdAndStatusAndEndBefore(
            Long itemId,
            Long userId,
            BookingStatus status,
            LocalDateTime currentTime
    );
}
