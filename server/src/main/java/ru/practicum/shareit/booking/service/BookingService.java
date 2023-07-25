package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;

public interface BookingService {

    ResponseBookingDto save(Long userId, RequestAddBookingDto requestAddBookingDto);

    ResponseBookingDto approve(Long userId, Long bookingId, Boolean approved);

    ResponseBookingDto findById(Long userId, Long bookingId);

    List<ResponseBookingDto> findByUserIdAndState(Long userId, String state, Integer from, Integer size);

    List<ResponseBookingDto> findByItemOwner(Long userId, String state, Integer from, Integer size);
}
