package ru.practicum.shareit.exception.model;

public class RequestNotFoundException extends EntityNotFoundException {
    public RequestNotFoundException(String message) {
        super(message);
    }
}
