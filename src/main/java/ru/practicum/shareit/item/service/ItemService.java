package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto findById(Long id);

    List<ItemDto> findByUserId(Long userId);

    ItemDto update(Long userId, Long id, ItemUpdateDto itemUpdateDto);

    void deleteById(Long id);

    List<ItemDto> findByNameOrDescription(String text);
}
