package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.ResponseBookingInItemDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    ResponseBookingInItemDto lastBooking;
    ResponseBookingInItemDto nextBooking;
    Long requestId;
}
