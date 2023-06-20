package ru.practicum.shareit.exception.booking;

public class BookingNotOwnerOperationException extends RuntimeException {
    public BookingNotOwnerOperationException(String message) {
        super(message);
    }
}
