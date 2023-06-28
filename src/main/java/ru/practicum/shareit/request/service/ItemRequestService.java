package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestAddItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {

    ResponseItemRequestDto save(Long userId, RequestAddItemRequestDto requestAddItemRequestDto);

    List<ResponseItemRequestWithItemsDto> findByUserId(Long userId);

    List<ResponseItemRequestWithItemsDto> findAllNotOwned(Long userId, Integer from, Integer size);
}
