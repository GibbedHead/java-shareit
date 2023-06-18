package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

public interface BookingService {

    ResponseBookingDto save(Long userId, RequestAddBookingDto requestAddBookingDto);
}
