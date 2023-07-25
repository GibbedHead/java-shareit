package ru.practicum.shareit.exception.booking;

public class BookingItemNotAvailableException extends RuntimeException {
    public BookingItemNotAvailableException(String message) {
        super(message);
    }
}
