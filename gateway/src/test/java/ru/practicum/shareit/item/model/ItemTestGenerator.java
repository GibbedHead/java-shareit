package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.RequestAddItemDto;

public class ItemTestGenerator {
    public static RequestAddItemDto getNullNameItem() {
        return new RequestAddItemDto(
                null,
                "description",
                true,
                null,
                null
        );
    }

    public static RequestAddItemDto getEmptyNameItem() {
        return new RequestAddItemDto(
                "",
                "description",
                true,
                null,
                null
        );
    }

    public static RequestAddItemDto getNullDescriptionIItem() {
        return new RequestAddItemDto(
                "item",
                null,
                true,
                null,
                null
        );
    }

    public static RequestAddItemDto getEmptyDescriptionItem() {
        return new RequestAddItemDto(
                "item",
                "",
                true,
                null,
                null
        );
    }

    public static RequestAddItemDto getNullAvailabilityItem() {
        return new RequestAddItemDto(
                "item",
                "description",
                null,
                null,
                null
        );
    }

    public static RequestAddItemDto getItem() {
        return new RequestAddItemDto(
                "item",
                "description",
                true,
                null,
                null
        );
    }
}
