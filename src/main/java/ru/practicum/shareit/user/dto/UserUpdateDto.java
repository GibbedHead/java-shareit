package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserUpdateDto {
    private String name;
    @Email(message = "User email must be correct email")
    private String email;
}
