package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseItemForItemRequestDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
}
