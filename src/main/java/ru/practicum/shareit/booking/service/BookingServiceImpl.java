package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.valitador.BookingValidator;
import ru.practicum.shareit.exception.model.BookingNotFoundException;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private static final String ITEM_NOT_FOUND_MESSAGE = "Item id=%d not found";
    private static final String USER_NOT_FOUND_MESSAGE = "User id=%d not found";
    private static final String BOOKING_NOT_FOUND_MESSAGE = "Booking id=%d not found";

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Override
    public ResponseBookingDto save(Long userId, RequestAddBookingDto requestAddBookingDto) {
        BookingValidator.checkStartBeforeEndOrThrowException(requestAddBookingDto);
        Item item = getItemOrThrowException(requestAddBookingDto.getItemId());
        BookingValidator.checkBookingOwnItemAndThrowException(userId, item);
        BookingValidator.checkItemIsAvailableOrThrowException(item);
        User user = getUserOrThrowException(userId);
        Booking requestBooking = bookingMapper.addDtoToBooking(requestAddBookingDto);
        requestBooking.setItem(item);
        requestBooking.setBooker(user);
        requestBooking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(requestBooking);
        return bookingMapper.bookingToResponseDto(savedBooking);
    }

    @Override
    public ResponseBookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingOrThrowException(bookingId);
        User requestUser = getUserOrThrowException(userId);
        BookingValidator.checkRequestUserIsItemOwnerOrThrowException(requestUser, booking);
        BookingValidator.checkRequestUserIsBookingOwnerAndThrowException(requestUser, booking);
        BookingValidator.checkNotAlreadyApprovedAndOrThrowException(booking);
        makeApprove(booking, approved);
        return bookingMapper.bookingToResponseDto(
                bookingRepository.save(booking)
        );
    }

    @Override
    public ResponseBookingDto findById(Long userId, Long bookingId) {
        Booking booking = getBookingOrThrowException(bookingId);
        BookingValidator.checkRequestorIsBookingOwnerOrItemOwnerOrThrowException(userId, booking);
        return bookingMapper.bookingToResponseDto(booking);
    }

    @Override
    public List<ResponseBookingDto> findByUserIdAndState(Long userId, String state, Integer from, Integer size) {
        BookingState bookingState = BookingValidator.getStateOrThrowException(state);
        getUserOrThrowException(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> bookings = getBookingsByUserIdAndState(userId, bookingState, pageable);
        return bookings.stream()
                .map(bookingMapper::bookingToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseBookingDto> findByItemOwner(Long userId, String state, Integer from, Integer size) {
        BookingState bookingState = BookingValidator.getStateOrThrowException(state);
        getUserOrThrowException(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> bookings = getBookingsByOwnerIdAndState(userId, bookingState, pageable);
        return bookings.stream()
                .map(bookingMapper::bookingToResponseDto)
                .collect(Collectors.toList());
    }

    private List<Booking> getBookingsByOwnerIdAndState(Long userId, BookingState state, Pageable pageable) {
        switch (state) {
            case ALL:
                return bookingRepository.findAllByOwner(userId, pageable);
            case CURRENT:
                return bookingRepository.findAllByOwnerCurrent(userId, LocalDateTime.now(), pageable);
            case PAST:
                return bookingRepository.findAllByOwnerPast(userId, LocalDateTime.now(), pageable);
            case FUTURE:
                return bookingRepository.findAllByOwnerFuture(userId, LocalDateTime.now(), pageable);
            case WAITING:
                return bookingRepository.findAllByOwnerAndStatus(userId, BookingStatus.WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByOwnerAndStatus(userId, BookingStatus.REJECTED, pageable);
            default:
                return List.of();
        }
    }

    private List<Booking> getBookingsByUserIdAndState(Long userId, BookingState state, Pageable pageable) {
        switch (state) {
            case ALL:
                return bookingRepository.findByBooker_IdOrderByEndDesc(userId, pageable);
            case CURRENT:
                return bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        pageable
                );
            case PAST:
                return bookingRepository.findByBooker_IdAndEndIsBeforeOrderByEndDesc(
                        userId,
                        LocalDateTime.now(),
                        pageable
                );
            case FUTURE:
                return bookingRepository.findByBooker_IdAndStartIsAfterOrderByEndDesc(
                        userId,
                        LocalDateTime.now(),
                        pageable
                );
            case WAITING:
                return bookingRepository.findByBooker_IdAndStatusOrderByEndDesc(
                        userId,
                        BookingStatus.WAITING,
                        pageable
                );
            case REJECTED:
                return bookingRepository.findByBooker_IdAndStatusOrderByEndDesc(
                        userId,
                        BookingStatus.REJECTED,
                        pageable
                );
            default:
                return List.of();
        }
    }

    private void makeApprove(Booking booking, Boolean approved) {
        if (Boolean.TRUE.equals(approved)) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
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

    private Booking getBookingOrThrowException(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            log.error(String.format(BOOKING_NOT_FOUND_MESSAGE, bookingId));
            throw new BookingNotFoundException(String.format(BOOKING_NOT_FOUND_MESSAGE, bookingId));
        }
        return bookingOptional.get();
    }


}
