package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.RequestNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestAddItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private static final String REQUEST_NOT_FOUND_MESSAGE = "Request id=%d not found";
    private static final String USER_NOT_FOUND_MESSAGE = "User id=%d not found";

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);
    private final ItemService itemService;

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

    @Override
    public List<ResponseItemRequestWithItemsDto> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error(String.format(USER_NOT_FOUND_MESSAGE, userId));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByIdDesc(userId);
        return requests.stream()
                .map(itemRequestMapper::itemRequestToResponseWithItemsDto)
                .map(this::addItemsToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseItemRequestWithItemsDto> findAllNotOwned(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            log.error(String.format(USER_NOT_FOUND_MESSAGE, userId));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotOrderByIdDesc(userId, pageable);
        return requests.stream()
                .map(itemRequestMapper::itemRequestToResponseWithItemsDto)
                .map(this::addItemsToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseItemRequestWithItemsDto findById(Long userId, Long id) {
        if (!userRepository.existsById(userId)) {
            log.error(String.format(USER_NOT_FOUND_MESSAGE, userId));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(
                () -> {
                    log.error(String.format(REQUEST_NOT_FOUND_MESSAGE, id));
                    return new RequestNotFoundException(String.format(REQUEST_NOT_FOUND_MESSAGE, id));
                }
        );
        return addItemsToResponseDto(
                itemRequestMapper.itemRequestToResponseWithItemsDto(
                        itemRequest
                )
        );
    }

    private ResponseItemRequestWithItemsDto addItemsToResponseDto(ResponseItemRequestWithItemsDto dto) {
        dto.setItems(itemService.getResponseItemRequestWithItemsDtoByRequestId(dto.getId()));
        return dto;
    }
}
