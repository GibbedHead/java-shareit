package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.exception.model.NotUniqueFieldException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.user.dto.RequestAddUserDto;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void save_whenDuplicateEmail_thenNotUniqueFieldException() {
        RequestAddUserDto dto = new RequestAddUserDto("User", "user@email.com");
        when(userRepository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        final NotUniqueFieldException exception = assertThrows(
                NotUniqueFieldException.class,
                () -> userService.save(dto)
        );

        assertThat(exception.getMessage(), equalTo("Email must be unique"));
    }

    @Test
    void save_whenValidUser_thenReturnDto() {
        RequestAddUserDto dto = new RequestAddUserDto("User", "user@email.com");
        User user = new User(
                1L,
                "User",
                "user@email.com"
        );
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        ResponseUserDto responseUserDto = userService.save(dto);
        assertThat(responseUserDto.getId(), equalTo(1L));
    }

    @Test
    void findById_whenWrongUserId_thenUserNotFoundException() {
        Long wrongUserId = 100L;
        when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findById(wrongUserId)
        );
        assertThat(exception.getMessage(), equalTo(String.format("User id=%d not found", wrongUserId)));
    }

    @Test
    void findById_whenValidId_thenReturnDto() {
        Long userId = 1L;
        User user = new User(
                userId,
                "User",
                "user@email.com"
        );
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        ResponseUserDto responseUserDto = userService.findById(userId);

        assertThat(responseUserDto.getId(), equalTo(userId));
    }

    @Test
    void update_whenWrongUserId_thenUserNotFoundException() {
        Long wrongUserId = 100L;
        RequestUpdateUserDto requestUpdateUserDto = new RequestUpdateUserDto(
                null,
                "NewName",
                "newemail@dom.com"
        );
        when(userRepository.findById(wrongUserId))
                .thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.update(wrongUserId, requestUpdateUserDto)
        );
        assertThat(exception.getMessage(), equalTo(String.format("User id=%d not found", wrongUserId)));
    }

    @Test
    void update_whenDuplicateEmail_thenNotUniqueFieldException() {
        Long userId = 1L;
        User user = new User(
                userId,
                "User",
                "user@email.com"
        );
        RequestUpdateUserDto requestUpdateUserDto = new RequestUpdateUserDto(
                userId,
                "NewName",
                "newemail@dom.com"
        );
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        final NotUniqueFieldException exception = assertThrows(
                NotUniqueFieldException.class,
                () -> userService.update(userId, requestUpdateUserDto)
        );
        assertThat(exception.getMessage(), equalTo("Email must be unique"));
    }

    @Test
    void update_whenValidUser_thenReturnDto() {
        Long userId = 1L;
        User user = new User(
                userId,
                "User",
                "user@email.com"
        );
        RequestUpdateUserDto requestUpdateUserDto = new RequestUpdateUserDto(
                userId,
                "NewName",
                "newemail@dom.com"
        );
        User updatedUser = new User(
                userId,
                "NewName",
                "newemail@dom.com"
        );
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.save(updatedUser))
                .thenReturn(updatedUser);

        ResponseUserDto responseUserDto = userService.update(userId, requestUpdateUserDto);
        assertThat(responseUserDto.getName(), equalTo("NewName"));
    }

    @Test
    void findAll_whenInvoke_returnListOfDtos() {
        Long userId = 1L;
        User user = new User(
                userId,
                "User",
                "user@email.com"
        );
        when(userRepository.findAll())
                .thenReturn(List.of(user));


        List<ResponseUserDto> all = userService.findAll();
        assertThat(all.size(), equalTo(1));
    }

    @Test
    void delete_whenValidUserId_thenNoExceptions() {
        assertDoesNotThrow(() -> userService.deleteById(1L));
    }

    @Test
    void delete_whenUserNotFound_thenUserNotFoundException() {
        doThrow(new EmptyResultDataAccessException(1)).when(userRepository).deleteById(isA(Long.class));
        assertThrows(UserNotFoundException.class, () -> userService.deleteById(1L));
    }
}