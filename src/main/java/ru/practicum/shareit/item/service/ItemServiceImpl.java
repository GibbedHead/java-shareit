package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        Optional<User> optionalUser = userStorage.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("User id=%d not found", userId));
        }
        itemDto.setOwner(optionalUser.get());
        return ItemMapper.toItemDto(
                itemStorage.save(
                        ItemMapper.toItem(itemDto)
                )
        );
    }

    @Override
    public ItemDto findById(long id) {
        return null;
    }

    @Override
    public List<ItemDto> findAll() {
        return null;
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemUpdateDto itemUpdateDto) {
        Optional<User> requestUser = userStorage.findById(userId);
        if (requestUser.isEmpty()) {
            throw new UserNotFoundException(String.format("User id=%d not found", userId));
        }
        Optional<Item> requestedItem = itemStorage.findById(id);
        if (requestedItem.isEmpty()) {
            throw new ItemNotFoundException(String.format("Item id=%d not found", id));
        }
        if (!Objects.equals(requestUser.get().getId(), requestedItem.get().getOwner().getId())) {
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
    public void deleteById(long id) {

    }
}
