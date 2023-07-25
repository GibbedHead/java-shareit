package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.RequestAddUserDto;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;

import java.util.List;

public interface UserService {
    ResponseUserDto save(RequestAddUserDto userDto);

    ResponseUserDto findById(Long id);

    List<ResponseUserDto> findAll();

    ResponseUserDto update(Long userId, RequestUpdateUserDto userDto);

    void deleteById(Long id);
}
