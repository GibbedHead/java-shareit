package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    @NotBlank(message = "Item name must not be blank")
    String name;
    @NotBlank(message = "Item description must not be blank")
    String description;
    @NotNull(message = "Item availability must not be empty")
    Boolean available;
    User owner;
    ItemRequest request;
}
