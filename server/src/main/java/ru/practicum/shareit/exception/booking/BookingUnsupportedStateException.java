package ru.practicum.shareit.exception.booking;

public class BookingUnsupportedStateException extends RuntimeException {
    public BookingUnsupportedStateException(String message) {
        super(message);
    }
}
