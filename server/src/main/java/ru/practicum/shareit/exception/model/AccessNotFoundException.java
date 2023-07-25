package ru.practicum.shareit.exception.model;

public class AccessNotFoundException extends RuntimeException {
    public AccessNotFoundException(String message) {
        super(message);
    }
}
