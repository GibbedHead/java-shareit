package ru.practicum.shareit.item.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item addDtoToItem(RequestAddItemDto itemDto);

    ResponseItemDto itemToResponseDto(Item item);

    ResponseItemWithCommentsDto itemToResponseWithCommentDto(Item item);

    ResponseItemForItemRequestDto itemToResponseItemForItemRequestDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromRequestUpdateDto(RequestUpdateItemDto itemDto, @MappingTarget Item item);
}