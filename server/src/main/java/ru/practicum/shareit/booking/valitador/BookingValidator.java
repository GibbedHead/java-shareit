package ru.practicum.shareit.booking.valitador;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.booking.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingValidator {
    private static final String ITEM_NOT_AVAILABLE_MESSAGE = "Item id=%d is not available";
    private static final String IS_NOT_OWNER_MESSAGE = "User id=%d is not item owner";
    private static final String NOT_SUPPORTED_OPERATION_MESSAGE = "Operation is not supported";


    public static void checkStartBeforeEndOrThrowException(RequestAddBookingDto dto) {
        if (
                dto.getStart().isAfter(dto.getEnd())
                        ||
                        dto.getStart().isEqual(dto.getEnd())
        ) {
            throw new BookingDateException("Start date must be before end date");
        }
    }

    public static void checkItemIsAvailableOrThrowException(Item item) {
        if (!item.getAvailable()) {
            log.error(String.format(ITEM_NOT_AVAILABLE_MESSAGE, item.getId()));
            throw new BookingItemNotAvailableException(String.format(ITEM_NOT_AVAILABLE_MESSAGE, item.getId()));
        }
    }

    public static void checkRequestUserIsItemOwnerOrThrowException(User requestUser, Booking booking) {
        if (!Objects.equals(requestUser.getId(), booking.getItem().getOwnerId())) {
            log.error(String.format(IS_NOT_OWNER_MESSAGE, requestUser.getId()));
            throw new BookingNotOwnerOperationException(String.format(IS_NOT_OWNER_MESSAGE, requestUser.getId()));
        }
    }

    public static void checkRequestUserIsBookingOwnerAndThrowException(User requestUser, Booking booking) {
        if (Objects.equals(requestUser.getId(), booking.getBooker().getId())) {
            log.error(NOT_SUPPORTED_OPERATION_MESSAGE);
            throw new BookingApprovingOwnBookingException(NOT_SUPPORTED_OPERATION_MESSAGE);
        }
    }

    public static void checkNotAlreadyApprovedAndOrThrowException(Booking booking) {
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.error(NOT_SUPPORTED_OPERATION_MESSAGE);
            throw new BookingAlreadyApprovedException(NOT_SUPPORTED_OPERATION_MESSAGE);
        }
    }

    public static void checkBookingOwnItemAndThrowException(Long userId, Item item) {
        if (Objects.equals(userId, item.getOwnerId())) {
            log.error(NOT_SUPPORTED_OPERATION_MESSAGE);
            throw new BookingOwnItemException(NOT_SUPPORTED_OPERATION_MESSAGE);
        }
    }

    public static void checkRequestorIsBookingOwnerOrItemOwnerOrThrowException(Long userId, Booking booking) {
        if (
                !(
                        Objects.equals(userId, booking.getItem().getOwnerId())
                                ||
                                Objects.equals(userId, booking.getBooker().getId())
                )
        ) {
            log.error(NOT_SUPPORTED_OPERATION_MESSAGE);
            throw new BookingNotOwnerOperationException(NOT_SUPPORTED_OPERATION_MESSAGE);
        }
    }
}
