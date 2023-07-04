package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.model.AccessBadRequestException;
import ru.practicum.shareit.exception.model.AccessNotFoundException;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerIT {
    private static final String USER_ID_HEADER_NAME = "X-Sharer-User-Id";
    private static final String URL = "/items";
    private static final String URL_WITH_ID = URL + "/{id}";
    private static final String URL_WITH_SEARCH = URL + "/search";
    private static final String URL_WITH_COMMENT = URL + "/{id}/comment";
    private static final String CONTENT_TYPE = "application/json";
    private final Long userId = 1L;
    private final Long itemId = 1L;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void save_whenValidUserIdAndValidItem_thenStatusCreatedAndItemDtoInBody() {
        ResponseItemDto responseItemDto = Instancio.create(ResponseItemDto.class);
        RequestAddItemDto requestAddItemDto = Instancio.create(RequestAddItemDto.class);
        when(itemService.save(anyLong(), any(RequestAddItemDto.class)))
                .thenReturn(responseItemDto);

        String response = mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddItemDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseItemDto)));
        verify(itemService, times(1)).save(anyLong(), any(RequestAddItemDto.class));
    }

    @SneakyThrows
    @Test
    void save_whenInvalidUserIdAndValidItem_thenStatusNotFound() {
        RequestAddItemDto requestAddItemDto = Instancio.create(RequestAddItemDto.class);
        when(itemService.save(anyLong(), any(RequestAddItemDto.class)))
                .thenThrow(UserNotFoundException.class);

        Long invalidUserId = 100L;
        mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddItemDto))
                                .header(USER_ID_HEADER_NAME, invalidUserId)
                )
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).save(anyLong(), any(RequestAddItemDto.class));
    }

    @SneakyThrows
    @Test
    void save_whenValidUserIdAndInvalidItem_thenStatusBadRequest() {
        RequestAddItemDto invalidRequestAddItemDto = Instancio.create(RequestAddItemDto.class);
        invalidRequestAddItemDto.setName("");
        when(itemService.save(anyLong(), any(RequestAddItemDto.class)))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(invalidRequestAddItemDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isBadRequest());

        verify(itemService, never()).save(anyLong(), any(RequestAddItemDto.class));
    }

    @SneakyThrows
    @Test
    void update_whenValidUserIdAndValidUpdateDto_thenStatusOkAndDtoInBody() {
        RequestUpdateItemDto requestUpdateItemDto = Instancio.create(RequestUpdateItemDto.class);
        ResponseItemDto responseItemDto = Instancio.create(ResponseItemDto.class);
        when(itemService.update(anyLong(), anyLong(), any(RequestUpdateItemDto.class)))
                .thenReturn(responseItemDto);

        String response = mockMvc.perform(
                        patch(URL_WITH_ID, itemId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestUpdateItemDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseItemDto)));
        verify(itemService, times(1)).update(anyLong(), anyLong(), any(RequestUpdateItemDto.class));
    }

    @SneakyThrows
    @Test
    void update_whenUserNotOwnerIdAndValidUpdateDto_thenStatusNotFound() {
        RequestUpdateItemDto requestUpdateItemDto = Instancio.create(RequestUpdateItemDto.class);
        when(itemService.update(anyLong(), anyLong(), any(RequestUpdateItemDto.class)))
                .thenThrow(AccessNotFoundException.class);

        mockMvc.perform(
                        patch(URL_WITH_ID, itemId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestUpdateItemDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).update(anyLong(), anyLong(), any(RequestUpdateItemDto.class));
    }

    @SneakyThrows
    @Test
    void update_whenValidUserIdAndItemNotFound_thenStatusNotFound() {
        RequestUpdateItemDto requestUpdateItemDto = Instancio.create(RequestUpdateItemDto.class);
        when(itemService.update(anyLong(), anyLong(), any(RequestUpdateItemDto.class)))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(
                        patch(URL_WITH_ID, itemId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestUpdateItemDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).update(anyLong(), anyLong(), any(RequestUpdateItemDto.class));
    }

    @SneakyThrows
    @Test
    void findById_whenValidUserIdAndValidItemId_thenStatusOkAndDtoWithCommentsInBody() {
        ResponseItemWithCommentsDto responseItemWithCommentsDto = Instancio.create(ResponseItemWithCommentsDto.class);
        when(itemService.findById(anyLong(), anyLong()))
                .thenReturn(responseItemWithCommentsDto);

        String response = mockMvc.perform(
                        get(URL_WITH_ID, itemId)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseItemWithCommentsDto)));
        verify(itemService, times(1)).findById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void findById_whenValidUserIdAndInvalidItemId_thenStatusNotFound() {
        when(itemService.findById(anyLong(), anyLong()))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(
                        get(URL_WITH_ID, itemId)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).findById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void findByUserId_whenValidUserIdAndValidPaging_thenStatusOkAndDtosListInResponse() {
        List<ResponseItemWithCommentsDto> responseItemWithCommentsDtos = Instancio
                .ofList(ResponseItemWithCommentsDto.class)
                .size(2)
                .create();

        when(itemService.findByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(responseItemWithCommentsDtos);

        String response = mockMvc.perform(
                        get(URL)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseItemWithCommentsDtos)));
        verify(itemService, times(1)).findByUserId(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void delete_whenValidUserId_thenStatusNoContent() {
        mockMvc.perform(
                        delete(URL_WITH_ID, itemId)
                                .content(CONTENT_TYPE)
                )
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteById(anyLong());
    }

    @SneakyThrows
    @Test
    void delete_whenInvalidUserId_thenStatusNotFound() {
        doThrow(ItemNotFoundException.class).when(itemService).deleteById(anyLong());

        mockMvc.perform(
                        delete(URL_WITH_ID, itemId)
                                .content(CONTENT_TYPE)
                )
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).deleteById(anyLong());
    }

    @SneakyThrows
    @Test
    void findByNameOrDescription_whenValidPaging_thenStatusOkAndDtosListInResponse() {
        List<ResponseItemDto> responseItemDtos = Instancio
                .ofList(ResponseItemDto.class)
                .size(2)
                .create();
        when(itemService.findByNameOrDescription(anyString(), anyInt(), anyInt()))
                .thenReturn(responseItemDtos);

        String response = mockMvc.perform(
                        get(URL_WITH_SEARCH)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("text", "search")
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseItemDtos)));
        verify(itemService, times(1)).findByNameOrDescription(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void saveComment_whenUserIsBookerUserExistsItemExists_thenStatusOkAndCommentDtoInResponse() {
        RequestAddCommentDto requestAddCommentDtos = Instancio.create(RequestAddCommentDto.class);
        ResponseCommentDto responseCommentDto = Instancio.create(ResponseCommentDto.class);
        when(itemService.saveComment(anyLong(), anyLong(), any(RequestAddCommentDto.class)))
                .thenReturn(responseCommentDto);

        String response = mockMvc.perform(
                        post(URL_WITH_COMMENT, itemId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddCommentDtos))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseCommentDto)));
        verify(itemService, times(1)).saveComment(
                anyLong(),
                anyLong(),
                any(RequestAddCommentDto.class)
        );
    }

    @SneakyThrows
    @Test
    void saveComment_whenUserIsNotBookerUserExistsItemExists_thenStatusBadRequest() {
        RequestAddCommentDto requestAddCommentDtos = Instancio.create(RequestAddCommentDto.class);
        when(itemService.saveComment(anyLong(), anyLong(), any(RequestAddCommentDto.class)))
                .thenThrow(AccessBadRequestException.class);

        mockMvc.perform(
                        post(URL_WITH_COMMENT, itemId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddCommentDtos))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isBadRequest());

        verify(itemService, times(1)).saveComment(
                anyLong(),
                anyLong(),
                any(RequestAddCommentDto.class)
        );
    }

    @SneakyThrows
    @Test
    void saveComment_whenUserIsBookerUserNotExistsItemExists_thenStatusNotFound() {
        RequestAddCommentDto requestAddCommentDtos = Instancio.create(RequestAddCommentDto.class);
        when(itemService.saveComment(anyLong(), anyLong(), any(RequestAddCommentDto.class)))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        post(URL_WITH_COMMENT, itemId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddCommentDtos))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).saveComment(
                anyLong(),
                anyLong(),
                any(RequestAddCommentDto.class)
        );
    }

    @SneakyThrows
    @Test
    void saveComment_whenUserIsBookerUserExistsItemNotExists_thenStatusNotFound() {
        RequestAddCommentDto requestAddCommentDtos = Instancio.create(RequestAddCommentDto.class);
        when(itemService.saveComment(anyLong(), anyLong(), any(RequestAddCommentDto.class)))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(
                        post(URL_WITH_COMMENT, itemId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddCommentDtos))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).saveComment(
                anyLong(),
                anyLong(),
                any(RequestAddCommentDto.class)
        );
    }
}