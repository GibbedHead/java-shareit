package ru.practicum.shareit.user.maper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void addDtoToUser_whenDtoNull_thenReturnNull() {
        assertNull(userMapper.addDtoToUser(null));
    }

    @Test
    void userToResponseDto_whenUerNull_thenReturnNull() {
        assertNull(userMapper.userToResponseDto(null));
    }

    @Test
    void updateUserFromRequestUpdateDto_whenDtoNull_thenNoUpdateForUserFields() {
        User user = new User(
                1L,
                "name",
                "email"
        );
        userMapper.updateUserFromRequestUpdateDto(null, user);

        assertThat("name", equalTo(user.getName()));
    }

    @Test
    void updateUserFromRequestUpdateDtoNullFields_whenDtoNull_thenNoUpdateForUserFields() {
        User user = new User(
                1L,
                "name",
                "email"
        );
        RequestUpdateUserDto updateUserDto = new RequestUpdateUserDto(
                null,
                null,
                null
        );
        userMapper.updateUserFromRequestUpdateDto(updateUserDto, user);

        assertThat(1L, equalTo(user.getId()));
        assertThat("name", equalTo(user.getName()));
        assertThat("email", equalTo(user.getEmail()));
    }

}