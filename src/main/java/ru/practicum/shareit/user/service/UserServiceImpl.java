package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotUniqueFieldException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.user.dto.RequestAddUserDto;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.maper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User id=%d not found";
    private final UserRepository userRepository;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Override
    public ResponseUserDto save(RequestAddUserDto requestAddUserDto) {
        try {
            return userMapper.userToResponseDto(
                    userRepository.save(
                            userMapper.addDtoToUser(requestAddUserDto)
                    )
            );
        } catch (DataIntegrityViolationException exception) {
            log.error("User not found. Email must be unique");
            throw new NotUniqueFieldException("Email must be unique");
        }
    }

    @Override
    public ResponseUserDto findById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error(String.format(USER_NOT_FOUND_MESSAGE, userId));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        return userMapper.userToResponseDto(userOptional.get());
    }

    @Override
    public List<ResponseUserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseUserDto update(Long userId, RequestUpdateUserDto requestUpdateUserDto) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error(String.format(USER_NOT_FOUND_MESSAGE, userId));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        userMapper.updateUserFromRequestUpdateDto(requestUpdateUserDto, userOptional.get());
        try {
            return userMapper.userToResponseDto(
                    userRepository.save(
                            userOptional.get()
                    )
            );
        } catch (DataIntegrityViolationException exception) {
            log.error("User not found. Email must be unique");
            throw new NotUniqueFieldException("Email must be unique");
        }
    }

    @Override
    public void deleteById(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            log.error(String.format(USER_NOT_FOUND_MESSAGE, userId));
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
    }
}
