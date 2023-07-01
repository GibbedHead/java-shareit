package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {
    private final ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void addDtoToItemRequest_whenDtoNull_thenReturnNull() {
        assertNull(itemRequestMapper.addDtoToItemRequest(null));
    }

    @Test
    void itemRequestToResponseDto_whenDtoNull_thenReturnNull() {
        assertNull(itemRequestMapper.itemRequestToResponseDto(null));
    }

    @Test
    void itemRequestToResponseWithItemsDto_whenDtoNull_thenReturnNull() {
        assertNull(itemRequestMapper.itemRequestToResponseWithItemsDto(null));
    }
}