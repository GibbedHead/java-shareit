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
import ru.practicum.shareit.exception.model.AccessException;
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
    public ResponseItemDto findById(Long userId, Long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            log.error(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, itemId));
        }
        ResponseItemDto dto = itemMapper.itemToResponseDto(itemOptional.get());
        if (Objects.equals(userId, itemOptional.get().getOwnerId())) {
            addBookingsToResponseDto(dto);
        }
        return dto;
    }

    @Override
    public List<ResponseItemDto> findByUserId(Long userId) {
        return itemRepository.findByOwnerIdOrderByIdAsc(userId).stream()
                .map(itemMapper::itemToResponseDto)
                .map(this::addBookingsToResponseDto)
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

    @Override
    public ResponseCommentDto saveComment(Long userId, Long itemId, RequestAddCommentDto addCommentDto) {
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

    private ResponseItemDto addBookingsToResponseDto(ResponseItemDto dto) {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> lastBooking = bookingRepository.findFirst1ByItem_IdAndEndLessThanAndStatusOrderByEndDesc(
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
