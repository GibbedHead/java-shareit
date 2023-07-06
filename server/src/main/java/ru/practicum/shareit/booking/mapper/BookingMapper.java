package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingInItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "itemId", target = "item")
    Booking addDtoToBooking(RequestAddBookingDto dto);

    ResponseBookingDto bookingToResponseDto(Booking booking);

    Item mapItemIdToUser(Long id);

    @Mapping(source = "booker", target = "bookerId")
    ResponseBookingInItemDto bookingToItemResponse(Booking booking);

    default Long mapUserToUserId(User user) {
        return user.getId();
    }
}
