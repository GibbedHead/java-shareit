package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.RequestAddUserDto;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    void save_whenInvoke_thenReturnDto() {
        RequestAddUserDto addUserDto = new RequestAddUserDto(
                "user1",
                "user1@email.com"
        );
        ResponseUserDto responseUserDto = new ResponseUserDto(
                1L,
                "user1",
                "user1@email.com"
        );

        when(userService.save(addUserDto))
                .thenReturn(responseUserDto);

        ResponseUserDto savedDto = userController.save(addUserDto);

        assertThat(1L, equalTo(savedDto.getId()));
    }

    @Test
    void update_whenInvoke_thenUserDto() {
        long userId = 1L;
        RequestUpdateUserDto updateUserDto = new RequestUpdateUserDto(
                null,
                "user1",
                "user1@email.com"
        );
        ResponseUserDto responseUserDto = new ResponseUserDto(
                1L,
                "user1",
                "user1@email.com"
        );

        when(userService.update(userId, updateUserDto))
                .thenReturn(responseUserDto);

        ResponseUserDto savedDto = userController.update(userId, updateUserDto);

        assertThat(1L, equalTo(savedDto.getId()));
    }

    @Test
    void findAll_whenInvoked_thenReturnDtosList() {
        when(userService.findAll())
                .thenReturn(List.of(new ResponseUserDto(
                        1L,
                        "User1",
                        "user1@email.com"
                )));

        List<ResponseUserDto> responseUserDtoList = userController.findAll();

        assertThat("User1", equalTo(responseUserDtoList.get(0).getName()));
    }

    @Test
    void findById_whenInvoked_thenReturnDto() {
        Long userId = 1L;
        ResponseUserDto responseUserDto = new ResponseUserDto(
                userId,
                "User1",
                "user1@email.com"
        );
        when(userService.findById(userId))
                .thenReturn(responseUserDto);

        ResponseUserDto userDto = userController.findById(userId);

        assertThat("User1", equalTo(userDto.getName()));
    }

    @Test
    void delete_whenInvoked_thenServiceDeleteByIdInvoked() {
        Long userId = 1L;
        userController.delete(userId);

        verify(userService, times(1)).deleteById(userId);
    }


}