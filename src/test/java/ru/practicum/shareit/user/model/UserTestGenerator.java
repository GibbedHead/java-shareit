package ru.practicum.shareit.user.model;

import ru.practicum.shareit.user.dto.RequestAddUserDto;

public class UserTestGenerator {
    public static RequestAddUserDto getNullNameUser() {
        return new RequestAddUserDto(
                0L,
                null,
                "user@test.com"
        );
    }

    public static RequestAddUserDto getEmptyNameUser() {
        return new RequestAddUserDto(
                0L,
                "",
                "user@test.com"
        );
    }

    public static RequestAddUserDto getNullEmailUser() {
        return new RequestAddUserDto(
                0L,
                "user",
                null
        );
    }

    public static RequestAddUserDto getEmptyEmailUser() {
        return new RequestAddUserDto(
                0L,
                "user",
                ""
        );
    }

    public static RequestAddUserDto getInvalidEmailUser() {
        return new RequestAddUserDto(
                0L,
                "user",
                "rrrrrrrr@"
        );
    }

    public static RequestAddUserDto getUser() {
        return new RequestAddUserDto(
                0L,
                "user",
                "user@test.com"
        );
    }
}
