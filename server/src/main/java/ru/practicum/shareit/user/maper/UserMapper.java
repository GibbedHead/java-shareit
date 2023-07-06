package ru.practicum.shareit.user.maper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.dto.RequestAddUserDto;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User addDtoToUser(RequestAddUserDto dto);

    ResponseUserDto userToResponseDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequestUpdateDto(RequestUpdateUserDto dto, @MappingTarget User user);
}
