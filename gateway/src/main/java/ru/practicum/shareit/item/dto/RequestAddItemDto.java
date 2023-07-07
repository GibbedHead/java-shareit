package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestAddItemDto {
    @NotBlank(message = "Item name must not be blank")
    String name;
    @NotBlank(message = "Item description must not be blank")
    String description;
    @NotNull(message = "Item availability must not be empty")
    Boolean available;
    Long ownerId;
    Long requestId;
}
