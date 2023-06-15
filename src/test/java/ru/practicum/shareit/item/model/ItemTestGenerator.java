package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;

public class ItemTestGenerator {
    public static ItemDto getNullNameItem() {
        return new ItemDto(
                0L,
                null,
                "description",
                true,
                null,
                null
        );
    }

    public static ItemDto getEmptyNameItem() {
        return new ItemDto(
                0L,
                "",
                "description",
                true,
                null,
                null
        );
    }

    public static ItemDto getNullDescriptionIItem() {
        return new ItemDto(
                0L,
                "item",
                null,
                true,
                null,
                null
        );
    }

    public static ItemDto getEmptyDescriptionItem() {
        return new ItemDto(
                0L,
                "item",
                "",
                true,
                null,
                null
        );
    }

    public static ItemDto getNullAvailabilityItem() {
        return new ItemDto(
                0L,
                "item",
                "description",
                null,
                null,
                null
        );
    }

    public static ItemDto getItem() {
        return new ItemDto(
                0L,
                "item",
                "description",
                true,
                null,
                null
        );
    }
}
