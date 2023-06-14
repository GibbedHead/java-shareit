package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Item name must not be blank")
    private String name;
    @NotBlank(message = "Item description must not be blank")
    private String description;
    @NotNull(message = "Item availability must not be empty")
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
