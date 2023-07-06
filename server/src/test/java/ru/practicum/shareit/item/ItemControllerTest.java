package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void save_whenInvoked_thenReturnItemDto() {
        Long userId = 1L;
        RequestAddItemDto addItemDto = new RequestAddItemDto(
                "item",
                "desc",
                true,
                userId,
                null
        );
        ResponseItemDto savedResponseItemDto = new ResponseItemDto(
                1L,
                "item",
                "desc",
                true,
                null,
                null,
                null
        );

        when(itemService.save(userId, addItemDto))
                .thenReturn(savedResponseItemDto);

        ResponseItemDto itemDto = itemController.save(userId, addItemDto);

        assertThat(1L, equalTo(itemDto.getId()));
    }

    @Test
    void update_whenInvoked_thenReturnItemDto() {
        Long userId = 1L;
        Long itemId = 1L;
        RequestUpdateItemDto updateItemDto = new RequestUpdateItemDto(
                null,
                "item",
                "desc",
                true,
                null,
                null
        );
        ResponseItemDto updatedResponseItemDto = new ResponseItemDto(
                1L,
                "item",
                "desc",
                true,
                null,
                null,
                null
        );

        when(itemService.update(userId, itemId, updateItemDto))
                .thenReturn(updatedResponseItemDto);

        ResponseItemDto itemDto = itemController.update(userId, itemId, updateItemDto);

        assertThat(1L, equalTo(itemDto.getId()));
    }

    @Test
    void findById_whenInvoked_thenReturnItemDto() {
        Long userId = 1L;
        Long itemId = 1L;
        ResponseItemWithCommentsDto responseItemWithCommentsDto = new ResponseItemWithCommentsDto(
                1L,
                "item",
                "desc",
                true,
                null,
                null,
                null
        );

        when(itemService.findById(userId, itemId))
                .thenReturn(responseItemWithCommentsDto);

        ResponseItemWithCommentsDto itemDto = itemController.findById(userId, itemId);

        assertThat(1L, equalTo(itemDto.getId()));
    }

    @Test
    void findByUserId_whenInvoked_thenReturnListOfItemDto() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 20;
        ResponseItemWithCommentsDto responseItemWithCommentsDto = new ResponseItemWithCommentsDto(
                1L,
                "item",
                "desc",
                true,
                null,
                null,
                null
        );

        when(itemService.findByUserId(userId, from, size))
                .thenReturn(List.of(responseItemWithCommentsDto));

        List<ResponseItemWithCommentsDto> itemDto = itemController.findByUserId(userId, from, size);

        assertThat(1, equalTo(itemDto.size()));
        assertThat(1L, equalTo(itemDto.get(0).getId()));
    }

    @Test
    void delete_whenInvoked_thenServiceDeleteMethodInvoked() {
        itemController.delete(1L);

        verify(itemService, times(1)).deleteById(1L);
    }

    @Test
    void findByNameOrDescription_whenInvoked_thenReturnDtosList() {
        String text = "search";
        Integer from = 0;
        Integer size = 20;
        ResponseItemDto responseItemDto = new ResponseItemDto(
                1L,
                "name",
                "desc",
                true,
                null,
                null,
                null
        );

        when(itemService.findByNameOrDescription(text, from, size))
                .thenReturn(List.of(responseItemDto));

        List<ResponseItemDto> responseItemDtos = itemController.findByNameOrDescription(text, from, size);

        assertThat(1L, equalTo(responseItemDtos.get(0).getId()));
    }

    @Test
    void saveComment_whenInvoke_thenReturnCommentDto() {
        Long userId = 1L;
        Long itemId = 1L;
        String text = "Comment";
        RequestAddCommentDto requestAddCommentDto = new RequestAddCommentDto(text);

        when(itemService.saveComment(userId, itemId, requestAddCommentDto))
                .thenReturn(new ResponseCommentDto(
                        1L,
                        text,
                        "user1",
                        LocalDateTime.now()
                ));

        ResponseCommentDto commentDto = itemController.saveComment(userId, itemId, requestAddCommentDto);

        assertThat(text, equalTo(commentDto.getText()));
    }

}