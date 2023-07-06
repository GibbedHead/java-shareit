package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {
    private final BookingMapperImpl bookingMapper = new BookingMapperImpl();

    @Test
    void addDtoToBooking_whenDtoNull_thenReturnNull() {
        assertNull(bookingMapper.addDtoToBooking(null));
    }

    @Test
    void bookingToResponseDto_whenDtoNull_thenReturnNull() {
        assertNull(bookingMapper.bookingToResponseDto(null));
    }

    @Test
    void mapItemIdToUser_whenDtoNull_thenReturnNull() {
        assertNull(bookingMapper.mapItemIdToUser(null));
    }

    @Test
    void bookingToItemResponse_whenDtoNull_thenReturnNull() {
        assertNull(bookingMapper.bookingToItemResponse(null));
    }

    @Test
    void userToResponseBookerDto_whenDtoNull_thenReturnNull() {
        assertNull(bookingMapper.userToResponseBookerDto(null));
    }

    @Test
    void itemToResponseBookingItemDto_whenDtoNull_thenReturnNull() {
        assertNull(bookingMapper.itemToResponseBookingItemDto(null));
    }
}