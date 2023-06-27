package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.RequestAddItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequest addDtoToItemRequest(RequestAddItemRequestDto requestAddItemRequestDto);

    ResponseItemRequestDto itemRequestToResponseDto(ItemRequest itemRequest);

    ResponseItemRequestWithItemsDto itemRequestToResponseWithItemsDto(ItemRequest itemRequest);
}
