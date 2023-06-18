package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime current);

    List<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime current);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long userId,
            LocalDateTime current,
            LocalDateTime current1
    );

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :userId " +
            "ORDER BY b.start DESC"
    )
    List<Booking> findAllByOwner(Long userId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :userId " +
            "AND b.start < :currentTime " +
            "AND b.end > :currentTime " +
            "ORDER BY b.start DESC"
    )
    List<Booking> findAllByOwnerCurrent(Long userId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :userId " +
            "AND b.end < :currentTime " +
            "ORDER BY b.start DESC"
    )
    List<Booking> findAllByOwnerPast(Long userId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :userId " +
            "AND b.start > :currentTime " +
            "ORDER BY b.start DESC"
    )
    List<Booking> findAllByOwnerFuture(Long userId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.ownerId = :userId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC"
    )
    List<Booking> findAllByOwnerAndStatus(Long userId, BookingStatus status);

    List<Booking> findFirst1ByItem_IdAndEndLessThanAndStatusOrderByEndDesc(
            Long itemId,
            LocalDateTime currentTime,
            BookingStatus status
    );

    List<Booking> findFirst1ByItem_IdAndStartGreaterThanAndStatusOrderByStartAsc(
            Long itemId,
            LocalDateTime currentTime,
            BookingStatus status
    );
}
