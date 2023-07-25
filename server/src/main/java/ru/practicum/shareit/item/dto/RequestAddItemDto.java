package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestAddItemDto {
    String name;
    String description;
    Boolean available;
    Long ownerId;
    Long requestId;
}
