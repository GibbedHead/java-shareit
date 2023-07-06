package ru.practicum.shareit.exception.model;

public class NotUniqueFieldException extends RuntimeException {
    public NotUniqueFieldException(String message) {
        super(message);
    }
}
