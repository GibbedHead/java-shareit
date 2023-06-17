package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.AccessException;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.dto.RequestAddItemDto;
import ru.practicum.shareit.item.dto.RequestUpdateItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private static final String ITEM_NOT_FOUND_MESSAGE = "Item id=%d not found";
    private static final String USER_NOT_FOUND_MESSAGE = "User id=%d not found";
    private static final String REQUEST_NOT_FOUND_MESSAGE = "Request id=%d not found";

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Override
    public ResponseItemDto save(Long userId, RequestAddItemDto itemDto) {
        if (!userRepository.existsById(userId)) {
            log.error(String.format(USER_NOT_FOUND_MESSAGE, userId));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        itemDto.setOwnerId(userId);
        return itemMapper.itemToResponseDto(
                itemRepository.save(
                        itemMapper.addDtoToItem(itemDto)
                )
        );
    }

    @Override
    public ResponseItemDto findById(Long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.error(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
        }
        return itemMapper.itemToResponseDto(itemOptional.get());
    }

    @Override
    public List<ResponseItemDto> findByUserId(Long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(itemMapper::itemToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseItemDto update(Long userId, Long itemId, RequestUpdateItemDto itemDto) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.error(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
        }
        if (!Objects.equals(userId, itemOptional.get().getOwnerId())) {
            log.error("Wrong user id");
            throw new AccessException("Wrong user id");
        }
        itemMapper.updateItemFromRequestUpdateDto(itemDto, itemOptional.get());
        return itemMapper.itemToResponseDto(
                itemRepository.save(
                        itemOptional.get()
                )
        );
    }

    @Override
    public void deleteById(Long itemId) {
        try {
            itemRepository.deleteById(itemId);
        } catch (EmptyResultDataAccessException e) {
            log.error(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
            throw new UserNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
        }
    }

    @Override
    public List<ResponseItemDto> findByNameOrDescription(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAllByNameOrDescriptionContainingAndIsAvailableIgnoreCase(text).stream()
                .map(itemMapper::itemToResponseDto)
                .collect(Collectors.toList());
    }

}
