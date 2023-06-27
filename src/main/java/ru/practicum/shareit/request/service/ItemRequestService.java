package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestAddItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;

public interface ItemRequestService {

    ResponseItemRequestDto save(Long userId, RequestAddItemRequestDto requestAddItemRequestDto);
}
