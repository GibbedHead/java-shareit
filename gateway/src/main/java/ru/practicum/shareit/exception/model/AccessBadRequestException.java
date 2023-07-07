package ru.practicum.shareit.exception.model;

public class AccessBadRequestException extends RuntimeException {
    public AccessBadRequestException(String message) {
        super(message);
    }
}
