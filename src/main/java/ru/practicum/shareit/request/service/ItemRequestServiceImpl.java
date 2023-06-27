package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.request.dto.RequestAddItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private static final String ITEM_NOT_FOUND_MESSAGE = "Item id=%d not found";
    private static final String USER_NOT_FOUND_MESSAGE = "User id=%d not found";

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    @Override
    public ResponseItemRequestDto save(Long userId, RequestAddItemRequestDto requestAddItemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.error(String.format(USER_NOT_FOUND_MESSAGE, userId));
                    return new UserNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
                }
        );
        ItemRequest itemRequest = itemRequestMapper.addDtoToItemRequest(requestAddItemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestMapper.itemRequestToResponseDto(
                itemRequestRepository.save(itemRequest)
        );
    }
}
