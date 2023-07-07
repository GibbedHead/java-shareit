package ru.practicum.shareit.exception.booking;

public class BookingApprovingOwnBookingException extends RuntimeException {
    public BookingApprovingOwnBookingException(String message) {
        super(message);
    }
}
