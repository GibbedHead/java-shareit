package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.model.RequestNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.request.dto.RequestAddItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerIT {
    private static final String USER_ID_HEADER_NAME = "X-Sharer-User-Id";
    private static final String URL = "/requests";
    private static final String URL_WITH_ID = URL + "/{id}";
    private static final String URL_ALL = URL + "/all";
    private static final String CONTENT_TYPE = "application/json";
    private final Long userId = 1L;
    private final Long itemRequestId = 1L;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void save_whenUserExistsAndValidAddDto_thenStatusCreatedAndDtoInResponse() {
        RequestAddItemRequestDto requestAddItemRequestDto = Instancio.create(RequestAddItemRequestDto.class);
        ResponseItemRequestDto responseItemRequestDto = Instancio.create(ResponseItemRequestDto.class);
        when(itemRequestService.save(anyLong(), any(RequestAddItemRequestDto.class)))
                .thenReturn(responseItemRequestDto);

        String response = mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddItemRequestDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseItemRequestDto)));
        verify(itemRequestService, times(1)).save(anyLong(), any(RequestAddItemRequestDto.class));
    }

    @SneakyThrows
    @Test
    void save_whenUserNotExistsAndValidAddDto_thenStatusNotFound() {
        RequestAddItemRequestDto requestAddItemRequestDto = Instancio.create(RequestAddItemRequestDto.class);
        when(itemRequestService.save(anyLong(), any(RequestAddItemRequestDto.class)))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddItemRequestDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).save(anyLong(), any(RequestAddItemRequestDto.class));
    }

    @SneakyThrows
    @Test
    void save_whenUserExistsAndInvalidAddDto_thenStatusBadRequest() {
        RequestAddItemRequestDto requestAddItemRequestDto = Instancio.create(RequestAddItemRequestDto.class);
        requestAddItemRequestDto.setDescription("");
        when(itemRequestService.save(anyLong(), any(RequestAddItemRequestDto.class)))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddItemRequestDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).save(anyLong(), any(RequestAddItemRequestDto.class));
    }

    @SneakyThrows
    @Test
    void findByUserId_whenUserExist_thenStatusOkAndDtosInResponse() {
        List<ResponseItemRequestWithItemsDto> responseItemRequestWithItemsDtos = Instancio
                .ofList(ResponseItemRequestWithItemsDto.class)
                .size(2)
                .create();
        when(itemRequestService.findByUserId(anyLong()))
                .thenReturn(responseItemRequestWithItemsDtos);

        String response = mockMvc.perform(
                        get(URL)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseItemRequestWithItemsDtos)));
        verify(itemRequestService, times(1)).findByUserId(anyLong());
    }

    @SneakyThrows
    @Test
    void findByUserId_whenUserNotExist_thenStatusNotFound() {
        when(itemRequestService.findByUserId(anyLong()))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        get(URL)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).findByUserId(anyLong());
    }

    @SneakyThrows
    @Test
    void findAllNotOwned_whenUserExist_thenStatusOkAndDtosInResponse() {
        List<ResponseItemRequestWithItemsDto> responseItemRequestWithItemsDtos = Instancio
                .ofList(ResponseItemRequestWithItemsDto.class)
                .size(2)
                .create();
        when(itemRequestService.findAllNotOwned(anyLong(), anyInt(), anyInt()))
                .thenReturn(responseItemRequestWithItemsDtos);

        String response = mockMvc.perform(
                        get(URL_ALL)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseItemRequestWithItemsDtos)));
        verify(itemRequestService, times(1)).findAllNotOwned(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllNotOwned_whenUserNotExist_thenStatusNotFound() {
        when(itemRequestService.findAllNotOwned(anyLong(), anyInt(), anyInt()))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        get(URL_ALL)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).findAllNotOwned(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findById_whenUserExistAndRequestExist_thenStatusOkAndDtosInResponse() {
        ResponseItemRequestWithItemsDto responseItemRequestWithItemsDtos
                = Instancio.create(ResponseItemRequestWithItemsDto.class);
        when(itemRequestService.findById(anyLong(), anyLong()))
                .thenReturn(responseItemRequestWithItemsDtos);

        String response = mockMvc.perform(
                        get(URL_WITH_ID, itemRequestId)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseItemRequestWithItemsDtos)));
        verify(itemRequestService, times(1)).findById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void findById_whenUserNotExistAndRequestExist_thenStatusNotFound() {
        when(itemRequestService.findById(anyLong(), anyLong()))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        get(URL_WITH_ID, itemRequestId)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).findById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void findById_whenUserExistAndRequestNotExist_thenStatusNotFound() {
        when(itemRequestService.findById(anyLong(), anyLong()))
                .thenThrow(RequestNotFoundException.class);

        mockMvc.perform(
                        get(URL_WITH_ID, itemRequestId)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).findById(anyLong(), anyLong());
    }
}