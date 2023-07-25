package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertNull;

class CommentMapperTest {
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void addDtoToComment_whenDtoNull_thenReturnNull() {
        assertNull(commentMapper.addDtoToComment(null));
    }

    @Test
    void commentToResponseDto_whenCommentNull_thenReturnNull() {
        assertNull(commentMapper.commentToResponseDto(null));
    }

}