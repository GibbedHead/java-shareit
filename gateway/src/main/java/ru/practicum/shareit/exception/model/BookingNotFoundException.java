package ru.practicum.shareit.exception.model;

public class BookingNotFoundException extends EntityNotFoundException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}
