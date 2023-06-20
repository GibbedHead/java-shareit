package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.dto.RequestAddBookingDto;

import java.time.LocalDateTime;

public class BookingTestGenerator {
    public static RequestAddBookingDto getRequestAddBookingDto() {
        return new RequestAddBookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
    }

    public static RequestAddBookingDto getRequestAddBookingNullItemIdDto() {
        return new RequestAddBookingDto(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
    }

    public static RequestAddBookingDto getRequestAddBookingNullStartIdDto() {
        return new RequestAddBookingDto(
                1L,
                null,
                LocalDateTime.now().plusHours(2)
        );
    }

    public static RequestAddBookingDto getRequestAddBookingNullEndDto() {
        return new RequestAddBookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                null
        );
    }

    public static RequestAddBookingDto getRequestAddBookingPastStartDto() {
        return new RequestAddBookingDto(
                1L,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2)
        );
    }

    public static RequestAddBookingDto getRequestAddBookingPastEndDto() {
        return new RequestAddBookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().minusHours(2)
        );
    }
}
