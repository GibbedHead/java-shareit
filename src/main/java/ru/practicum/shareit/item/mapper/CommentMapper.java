package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.RequestAddCommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment addDtoToComment(RequestAddCommentDto dto);

    @Mapping(source = "author", target = "authorName")
    ResponseCommentDto commentToResponseDto(Comment comment);

    default String mapUserToUserName(User user) {
        return user.getName();
    }
}