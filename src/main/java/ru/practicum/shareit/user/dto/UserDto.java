package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserDto {
    Long id;
    @NotBlank(message = "User name must not be blank")
    private String name;
    @NotBlank(message = "User email must not be blank")
    @Email(message = "User email must be correct email")
    private String email;
}
