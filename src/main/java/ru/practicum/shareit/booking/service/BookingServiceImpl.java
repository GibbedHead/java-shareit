package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.booking.BookingDateException;
import ru.practicum.shareit.exception.booking.BookingItemNotAvailableException;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private static final String ITEM_NOT_FOUND_MESSAGE = "Item id=%d not found";
    private static final String USER_NOT_FOUND_MESSAGE = "User id=%d not found";
    private static final String ITEM_NOT_AVAILABLE_MESSAGE = "Item id=%d is not available";

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Override
    public ResponseBookingDto save(Long userId, RequestAddBookingDto requestAddBookingDto) {
        checkStartBeforeEndOrThrowException(requestAddBookingDto);
        Item item = getItemOrThrowException(requestAddBookingDto.getItemId());
        checkItemIsAvailableOrThrowException(item);
        User user = getUserOrThrowException(userId);
        Booking requestBooking = bookingMapper.addDtoToBooking(requestAddBookingDto);
        requestBooking.setItem(item);
        requestBooking.setBooker(user);
        requestBooking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(requestBooking);
        return bookingMapper.bookingToResponseDto(savedBooking);
    }

    private void checkStartBeforeEndOrThrowException(RequestAddBookingDto dto) {
        if (
                dto.getStart().isAfter(dto.getEnd())
                        ||
                        dto.getStart().isEqual(dto.getEnd())
        ) {
            throw new BookingDateException("Start date must be before end date");
        }
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

    private void checkItemIsAvailableOrThrowException(Item item) {
        if (item.getAvailable() != null && !item.getAvailable()) {
            log.error(String.format(ITEM_NOT_AVAILABLE_MESSAGE, item.getId()));
            throw new BookingItemNotAvailableException(String.format(ITEM_NOT_AVAILABLE_MESSAGE, item.getId()));
        }
    }

}
