package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.model.AccessBadRequestException;
import ru.practicum.shareit.exception.model.AccessNotFoundException;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
    private static final String NOT_ITEM_BOOKER_ALREADY_MESSAGE = "User id=%d not already booked itemId=%d";

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

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
    public ResponseItemWithCommentsDto findById(Long userId, Long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.error(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
        }
        ResponseItemWithCommentsDto dto = itemMapper.itemToResponseWithCommentDto(itemOptional.get());
        if (Objects.equals(userId, itemOptional.get().getOwnerId())) {
            addBookingsToResponseWithCommentDto(dto);
        }
        addCommentsResponseWithComment(dto);
        return dto;
    }

    @Override
    public List<ResponseItemWithCommentsDto> findByUserId(Long userId) {
        return itemRepository.findByOwnerIdOrderByIdAsc(userId).stream()
                .map(itemMapper::itemToResponseWithCommentDto)
                .map(this::addBookingsToResponseWithCommentDto)
                .map(this::addCommentsResponseWithComment)
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
            throw new AccessNotFoundException("Wrong user id");
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

    @Override
    public ResponseCommentDto saveComment(Long userId, Long itemId, RequestAddCommentDto addCommentDto) {
        if (isUserNotAlreadyItemBooker(userId, itemId)) {
            log.error(String.format(NOT_ITEM_BOOKER_ALREADY_MESSAGE, userId, itemId));
            throw new AccessBadRequestException(String.format(NOT_ITEM_BOOKER_ALREADY_MESSAGE, userId, itemId));
        }
        User user = getUserOrThrowException(userId);
        Item item = getItemOrThrowException(itemId);
        Comment comment = commentMapper.addDtoToComment(addCommentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.commentToResponseDto(
                commentRepository.save(comment)
        );
    }

    @Override
    public List<ResponseItemForItemRequestDto> getResponseItemRequestWithItemsDtoByRequestId(Long requestId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return items.stream()
                .map(itemMapper::itemToResponseItemForItemRequestDto)
                .collect(Collectors.toList());
    }

    private ResponseItemWithCommentsDto addCommentsResponseWithComment(ResponseItemWithCommentsDto dto) {
        dto.setComments(
                commentRepository.findByItem_Id(dto.getId()).stream()
                        .map(commentMapper::commentToResponseDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private boolean isUserNotAlreadyItemBooker(Long userId, Long itemId) {
        List<Booking> pastBookings = bookingRepository.findFirst1ByItem_IdAndBooker_IdAndStatusAndEndBefore(
                itemId,
                userId,
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );
        return pastBookings.isEmpty();
    }

    private ResponseItemWithCommentsDto addBookingsToResponseWithCommentDto(ResponseItemWithCommentsDto dto) {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> lastBooking = bookingRepository.findFirst1ByItem_IdAndStartLessThanAndStatusOrderByEndDesc(
                dto.getId(),
                currentTime,
                BookingStatus.APPROVED
        );
        List<Booking> nextBooking = bookingRepository.findFirst1ByItem_IdAndStartGreaterThanAndStatusOrderByStartAsc(
                dto.getId(),
                currentTime,
                BookingStatus.APPROVED
        );
        if (!lastBooking.isEmpty()) {
            dto.setLastBooking(bookingMapper.bookingToItemResponse(lastBooking.get(0)));
        }
        if (!nextBooking.isEmpty()) {
            dto.setNextBooking(bookingMapper.bookingToItemResponse(nextBooking.get(0)));
        }
        return dto;
    }

    private User getUserOrThrowException(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error(String.format(USER_NOT_FOUND_MESSAGE, userId));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        return userOptional.get();
    }

    private Item getItemOrThrowException(Long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.error(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
        }
        return itemOptional.get();
    }
}
