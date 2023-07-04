package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.RequestUpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemMapperTest {
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void addDtoToItem_whenItemDtoNull_thenReturnNull() {
        assertNull(itemMapper.addDtoToItem(null));
    }

    @Test
    void itemToResponseDto_whenItemNull_thenReturnNull() {
        assertNull(itemMapper.itemToResponseDto(null));
    }

    @Test
    void itemToResponseWithCommentDto_whenItemNull_thenReturnNull() {
        assertNull(itemMapper.itemToResponseWithCommentDto(null));
    }

    @Test
    void itemToResponseItemForItemRequestDto_whenItemNull_thenReturnNull() {
        assertNull(itemMapper.itemToResponseItemForItemRequestDto(null));
    }

    @Test
    void updateItemFromRequestUpdateDto_whenItemDtoNull_thenNoUpdate() {
        Item item = new Item(
                1L,
                "name",
                "desc",
                true,
                1L,
                1L
        );
        itemMapper.updateItemFromRequestUpdateDto(null, item);
        assertThat("name", equalTo(item.getName()));
    }

    @Test
    void updateItemFromRequestUpdateDto_whenItemDtoFieldsNull_thenNoUpdateForItemFields() {
        Item item = new Item(
                1L,
                "name",
                "desc",
                true,
                1L,
                1L
        );
        RequestUpdateItemDto updateItemDto = new RequestUpdateItemDto(
                null,
                null,
                null,
                null,
                null,
                null
        );
        itemMapper.updateItemFromRequestUpdateDto(updateItemDto, item);
        assertThat(1L, equalTo(item.getId()));
        assertThat("name", equalTo(item.getName()));
        assertThat("desc", equalTo(item.getDescription()));
        assertThat(true, equalTo(item.getAvailable()));
        assertThat(1L, equalTo(item.getOwnerId()));
        assertThat(1L, equalTo(item.getRequestId()));
    }
}