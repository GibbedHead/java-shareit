package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto findById(long id);

    List<UserDto> findAll();

    UserDto update(Long id, UserUpdateDto userDto);

    void deleteById(long id);
}
