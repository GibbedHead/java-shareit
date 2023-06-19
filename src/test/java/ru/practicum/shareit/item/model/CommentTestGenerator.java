package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.RequestAddCommentDto;

public class CommentTestGenerator {
    public static RequestAddCommentDto getAddCommentDto() {
        return new RequestAddCommentDto(
                "Text"
        );
    }

    public static RequestAddCommentDto getAddCommentNullTextDto() {
        return new RequestAddCommentDto(
                null
        );
    }

    public static RequestAddCommentDto getAddCommentEmptyTextDto() {
        return new RequestAddCommentDto(
                ""
        );
    }
}
