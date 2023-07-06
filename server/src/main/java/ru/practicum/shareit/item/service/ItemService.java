package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ResponseItemDto save(Long userId, RequestAddItemDto itemDto);

    ResponseItemWithCommentsDto findById(Long userId, Long id);

    List<ResponseItemWithCommentsDto> findByUserId(Long userId, Integer from, Integer size);

    ResponseItemDto update(Long userId, Long itemId, RequestUpdateItemDto itemUpdateDto);

    void deleteById(Long id);

    List<ResponseItemDto> findByNameOrDescription(String text, Integer from, Integer size);

    ResponseCommentDto saveComment(Long userId, Long itemId, RequestAddCommentDto addCommentDto);

    List<ResponseItemForItemRequestDto> getResponseItemRequestWithItemsDtoByRequestId(Long requestId);
}
