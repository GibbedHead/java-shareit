package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.ResponseBookingInItemDto;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseItemWithCommentsDto {
    Long id;
    String name;
    String description;
    Boolean available;
    ResponseBookingInItemDto lastBooking;
    ResponseBookingInItemDto nextBooking;
    List<ResponseCommentDto> comments;
}
