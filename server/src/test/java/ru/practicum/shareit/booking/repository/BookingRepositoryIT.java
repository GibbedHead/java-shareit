package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class BookingRepositoryIT {
    private final Pageable pageable = PageRequest.of(0, 20);
    private final LocalDateTime now = LocalDateTime.of(2023, 6, 27, 23, 34, 33);
    private final LocalDateTime nowPlusHour = now.plusHours(1);
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findByBooker_IdOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findByBooker_IdOrderByEndDesc(1L, pageable);

        assertThat(5, equalTo(bookings.size()));
        assertThat(2L, equalTo(bookings.get(0).getId()));
        assertThat(2L, equalTo(bookings.get(4).getItem().getId()));
    }

    @Test
    void findByBooker_IdAndStatusOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findByBooker_IdAndStatusOrderByEndDesc(
                1L,
                BookingStatus.APPROVED,
                pageable
        );

        assertThat(4, equalTo(bookings.size()));
        assertThat(2L, equalTo(bookings.get(0).getId()));
        assertThat(1L, equalTo(bookings.get(3).getId()));
    }

    @Test
    void findByBooker_IdAndEndIsBeforeOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findByBooker_IdAndEndIsBeforeOrderByEndDesc(
                1L,
                now,
                pageable
        );

        assertThat(2, equalTo(bookings.size()));
        assertThat(6L, equalTo(bookings.get(0).getId()));
        assertThat(1L, equalTo(bookings.get(1).getId()));
    }

    @Test
    void findByBooker_IdAndStartIsAfterOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findByBooker_IdAndEndIsBeforeOrderByEndDesc(
                1L,
                now,
                pageable
        );

        assertThat(2, equalTo(bookings.size()));
        assertThat(6L, equalTo(bookings.get(0).getId()));
        assertThat(1L, equalTo(bookings.get(1).getId()));
    }

    @Test
    void findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                1L,
                now,
                nowPlusHour,
                pageable
        );

        assertThat(1, equalTo(bookings.size()));
        assertThat(5L, equalTo(bookings.get(0).getId()));
    }

    @Test
    void findAllByOwner() {
        List<Booking> bookings = bookingRepository.findAllByOwner(
                1L,
                pageable
        );

        assertThat(2, equalTo(bookings.size()));
        assertThat(7L, equalTo(bookings.get(0).getId()));
        assertThat(3L, equalTo(bookings.get(1).getId()));
    }

    @Test
    void findAllByOwnerCurrent() {
        List<Booking> bookings = bookingRepository.findAllByOwnerCurrent(
                4L,
                now,
                pageable
        );

        assertThat(1, equalTo(bookings.size()));
        assertThat(5L, equalTo(bookings.get(0).getId()));
    }

    @Test
    void findAllByOwnerPast() {
        List<Booking> bookings = bookingRepository.findAllByOwnerPast(
                4L,
                now,
                pageable
        );

        assertThat(2, equalTo(bookings.size()));
        assertThat(6L, equalTo(bookings.get(0).getId()));
        assertThat(1L, equalTo(bookings.get(1).getId()));
    }

    @Test
    void findAllByOwnerFuture() {
        List<Booking> bookings = bookingRepository.findAllByOwnerFuture(
                4L,
                now,
                pageable
        );

        assertThat(1, equalTo(bookings.size()));
        assertThat(2L, equalTo(bookings.get(0).getId()));
    }

    @Test
    void findAllByOwnerAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByOwnerAndStatus(
                4L,
                BookingStatus.APPROVED,
                pageable
        );

        assertThat(4, equalTo(bookings.size()));
        assertThat(2L, equalTo(bookings.get(0).getId()));
        assertThat(1L, equalTo(bookings.get(3).getId()));
    }

    @Test
    void findFirst1ByItem_IdAndStartLessThanAndStatusOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findFirst1ByItem_IdAndStartLessThanAndStatusOrderByEndDesc(
                4L,
                now,
                BookingStatus.APPROVED
        );

        assertThat(1, equalTo(bookings.size()));
        assertThat(8L, equalTo(bookings.get(0).getId()));
    }

    @Test
    void findFirst1ByItem_IdAndStartGreaterThanAndStatusOrderByStartAsc() {
        List<Booking> bookings = bookingRepository.findFirst1ByItem_IdAndStartGreaterThanAndStatusOrderByStartAsc(
                1L,
                now,
                BookingStatus.APPROVED
        );

        assertThat(1, equalTo(bookings.size()));
        assertThat(7L, equalTo(bookings.get(0).getId()));
    }

    @Test
    void findFirst1ByItem_IdAndBooker_IdAndStatusAndEndBefore() {
        List<Booking> bookings = bookingRepository.findFirst1ByItem_IdAndBooker_IdAndStatusAndEndBefore(
                2L,
                1L,
                BookingStatus.APPROVED,
                now
        );

        assertThat(1, equalTo(bookings.size()));
        assertThat(1L, equalTo(bookings.get(0).getId()));
    }
}