package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto findById(long id);

    List<ItemDto> findAll();

    ItemDto update(Long userId, Long id, ItemUpdateDto itemUpdateDto);

    void deleteById(long id);
}
