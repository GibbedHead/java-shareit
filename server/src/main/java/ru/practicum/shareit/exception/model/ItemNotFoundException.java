package ru.practicum.shareit.exception.model;

public class ItemNotFoundException extends EntityNotFoundException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
