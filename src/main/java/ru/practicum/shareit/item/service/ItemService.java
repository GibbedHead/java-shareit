package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ResponseItemDto save(Long userId, RequestAddItemDto itemDto);

    ResponseItemDto findById(Long userId, Long id);

    List<ResponseItemDto> findByUserId(Long userId);

    ResponseItemDto update(Long userId, Long itemId, RequestUpdateItemDto itemUpdateDto);

    void deleteById(Long id);

    List<ResponseItemDto> findByNameOrDescription(String text);

    ResponseCommentDto saveComment(Long userId, Long itemId, RequestAddCommentDto addCommentDto);
}
