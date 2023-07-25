package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ResponseItemForItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseItemRequestWithItemsDto {
    Long id;
    String description;
    LocalDateTime created;
    List<ResponseItemForItemRequestDto> items;
}
