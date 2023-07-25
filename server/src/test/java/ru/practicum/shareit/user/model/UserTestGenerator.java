package ru.practicum.shareit.user.model;

import ru.practicum.shareit.user.dto.RequestAddUserDto;

public class UserTestGenerator {
    public static RequestAddUserDto getNullNameUser() {
        return new RequestAddUserDto(
                null,
                "user@test.com"
        );
    }

    public static RequestAddUserDto getEmptyNameUser() {
        return new RequestAddUserDto(
                "",
                "user@test.com"
        );
    }

    public static RequestAddUserDto getNullEmailUser() {
        return new RequestAddUserDto(
                "user",
                null
        );
    }

    public static RequestAddUserDto getEmptyEmailUser() {
        return new RequestAddUserDto(
                "user",
                ""
        );
    }

    public static RequestAddUserDto getInvalidEmailUser() {
        return new RequestAddUserDto(
                "user",
                "rrrrrrrr@"
        );
    }

    public static RequestAddUserDto getUser1() {
        return new RequestAddUserDto(
                "user1",
                "user1@test.com"
        );
    }

    public static RequestAddUserDto getUser2() {
        return new RequestAddUserDto(
                "user2",
                "user2@test.com"
        );
    }
}
