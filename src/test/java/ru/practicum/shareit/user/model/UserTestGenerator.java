package ru.practicum.shareit.user.model;

import ru.practicum.shareit.user.dto.UserDto;

public class UserTestGenerator {
    public static UserDto getNullNameUser() {
        return new UserDto(
                0L,
                null,
                "user@test.com"
        );
    }

    public static UserDto getEmptyNameUser() {
        return new UserDto(
                0L,
                "",
                "user@test.com"
        );
    }

    public static UserDto getNullEmailUser() {
        return new UserDto(
                0L,
                "user",
                null
        );
    }

    public static UserDto getEmptyEmailUser() {
        return new UserDto(
                0L,
                "user",
                ""
        );
    }

    public static UserDto getInvalidEmailUser() {
        return new UserDto(
                0L,
                "user",
                "rrrrrrrr@"
        );
    }

    public static UserDto getUser() {
        return new UserDto(
                0L,
                "user",
                "user@test.com"
        );
    }
}
