package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotUniqueFieldException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.maper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto save(UserDto userDto) {
        if (userStorage.isNotUniqueEmail(userDto.getEmail(), userDto.getId())) {
            throw new NotUniqueFieldException("Email must be unique");
        }
        return UserMapper.toUserDto(
                userStorage.save(
                        UserMapper.toUser(userDto)
                )
        );
    }

    @Override
    public UserDto findById(long id) {
        Optional<User> userOptional = userStorage.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("User id=%d not found", id));
        }
        return UserMapper.toUserDto(userOptional.get());
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, UserUpdateDto userDto) {
        Optional<User> userOptional = userStorage.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("User id=%d not found", id));
        }
        if (userStorage.isNotUniqueEmail(userDto.getEmail(), id)) {
            throw new NotUniqueFieldException("Email must be unique");
        }
        return UserMapper.toUserDto(
                userStorage.update(
                        UserMapper.updateUserDtoToUser(userOptional.get(), userDto)
                )
        );
    }

    @Override
    public void deleteById(long id) {
        userStorage.deleteById(id);
    }
}
