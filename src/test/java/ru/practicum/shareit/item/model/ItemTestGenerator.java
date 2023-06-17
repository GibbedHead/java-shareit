package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.RequestAddItemDto;

public class ItemTestGenerator {
    public static RequestAddItemDto getNullNameItem() {
        return new RequestAddItemDto(
                0L,
                null,
                "description",
                true,
                null,
                null
        );
    }

    public static RequestAddItemDto getEmptyNameItem() {
        return new RequestAddItemDto(
                0L,
                "",
                "description",
                true,
                null,
                null
        );
    }

    public static RequestAddItemDto getNullDescriptionIItem() {
        return new RequestAddItemDto(
                0L,
                "item",
                null,
                true,
                null,
                null
        );
    }

    public static RequestAddItemDto getEmptyDescriptionItem() {
        return new RequestAddItemDto(
                0L,
                "item",
                "",
                true,
                null,
                null
        );
    }

    public static RequestAddItemDto getNullAvailabilityItem() {
        return new RequestAddItemDto(
                0L,
                "item",
                "description",
                null,
                null,
                null
        );
    }

    public static RequestAddItemDto getItem() {
        return new RequestAddItemDto(
                0L,
                "item",
                "description",
                true,
                null,
                null
        );
    }
}
