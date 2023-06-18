package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "itemId", target = "item")
    Booking addDtoToBooking(RequestAddBookingDto dto);

    ResponseBookingDto bookingToResponseDto(Booking booking);

    Item mapItemIdToUser(Long id);
}
