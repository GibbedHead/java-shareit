package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest request;
}
