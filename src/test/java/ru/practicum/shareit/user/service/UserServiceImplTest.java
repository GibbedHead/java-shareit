package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.model.NotUniqueFieldException;
import ru.practicum.shareit.user.dto.RequestAddUserDto;
import ru.practicum.shareit.user.maper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;

class UserServiceImplTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserMapper userMapper = Mockito.mock(UserMapper.class);

    @Test
    void save() {
        RequestAddUserDto dto = new RequestAddUserDto("User", "user@email.com");
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);
        UserService userService = new UserServiceImpl(userRepository);

        final NotUniqueFieldException exception = Assertions.assertThrows(
                NotUniqueFieldException.class,
                () -> userService.save(dto)
        );
        assertThat(exception.getMessage(), equalTo("Email must be unique"));
    }
}