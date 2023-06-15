package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.AccessException;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        Optional<User> optionalUser = userStorage.findById(userId);
        if (optionalUser.isEmpty()) {
            log.error(String.format("User id=%d not found", userId));
            throw new UserNotFoundException(String.format("User id=%d not found", userId));
        }
        itemDto.setOwner(optionalUser.get());
        log.info(String.format("Item saved: %s", itemDto));
        return ItemMapper.toItemDto(
                itemStorage.save(
                        ItemMapper.toItem(itemDto)
                )
        );
    }

    @Override
    public ItemDto findById(Long id) {
        Optional<Item> itemOptional = itemStorage.findById(id);
        if (itemOptional.isEmpty()) {
            log.error(String.format("Item id=%d not found", id));
            throw new ItemNotFoundException(String.format("Item id=%d not found", id));
        }
        return ItemMapper.toItemDto(itemOptional.get());
    }

    @Override
    public List<ItemDto> findByUserId(Long userId) {
        return itemStorage.findByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemUpdateDto itemUpdateDto) {
        Optional<User> requestUser = userStorage.findById(userId);
        if (requestUser.isEmpty()) {
            log.error(String.format("User id=%d not found", userId));
            throw new UserNotFoundException(String.format("User id=%d not found", userId));
        }
        Optional<Item> requestedItem = itemStorage.findById(id);
        if (requestedItem.isEmpty()) {
            log.error(String.format("Item id=%d not found", id));
            throw new ItemNotFoundException(String.format("Item id=%d not found", id));
        }
        if (!Objects.equals(requestUser.get().getId(), requestedItem.get().getOwner().getId())) {
            log.error("Wrong user id");
            throw new AccessException("Wrong user id");
        }
        return ItemMapper.toItemDto(
                itemStorage.update(
                        ItemMapper.updateItemDtoToItem(
                                requestedItem.get(), itemUpdateDto
                        )
                )
        );
    }

    @Override
    public void deleteById(Long id) {
        itemStorage.deleteById(id);
    }

    @Override
    public List<ItemDto> findByNameOrDescription(String text) {
        if (text.isEmpty()) {
            log.info("Empty search result");
            return List.of();
        }
        return itemStorage.findByNameOrDescription(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
