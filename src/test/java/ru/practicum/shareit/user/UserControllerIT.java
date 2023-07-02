package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.model.NotUniqueFieldException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.user.dto.RequestAddUserDto;
import ru.practicum.shareit.user.dto.RequestUpdateUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerIT {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    private static final String URL = "/users";
    private static final String URL_WITH_ID = URL + "/{id}";
    private static final String CONTENT_TYPE = "application/json";
    private final Long userId = 1L;
    private final Long invalidUserId = 100L;
    private final ResponseUserDto responseUserDto = new ResponseUserDto(
            userId,
            "name",
            "user@email.com"
    );
    private final ResponseUserDto responseUpdatedUserDto = new ResponseUserDto(
            userId,
            "updateName",
            "update@email.com"
    );
    private final List<ResponseUserDto> responseUserDtos = List.of(new ResponseUserDto(
            userId,
            "name",
            "user@email.com"
    ));
    private final RequestAddUserDto requestAddUserDto = new RequestAddUserDto(
            "name",
            "user@email.com"
    );
    private final RequestAddUserDto requestInvalidAddUserDto = new RequestAddUserDto(
            null,
            "user.email.com"
    );
    private final RequestUpdateUserDto requestUpdateUserDto = new RequestUpdateUserDto(
            1L,
            "updateName",
            "update@email.com"
    );
    private final RequestUpdateUserDto requestDuplicateEmailUpdateUserDto = new RequestUpdateUserDto(
            1L,
            "updateName",
            "user2@email.com"
    );
    private final RequestUpdateUserDto requestInvalidUpdateUserDto = new RequestUpdateUserDto(
            1L,
            "",
            "user1email.com"
    );

    @SneakyThrows
    @Test
    void save_whenValidUser_thenStatusCreatedAndUserDtoInBody() {
        when(userService.save(any(RequestAddUserDto.class)))
                .thenReturn(responseUserDto);

        String response = mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddUserDto))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(objectMapper.writeValueAsString(responseUserDto), equalTo(response));
        verify(userService, times(1)).save(any(RequestAddUserDto.class));
    }

    @SneakyThrows
    @Test
    void save_whenInvalidUser_thenStatusBadRequestAndErrorInfoSent() {
        String response = mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestInvalidAddUserDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expected = "{\"errors\":{\"name\":\"User name must not be blank\",\"email\":\"User email must be correct email\"}}";
        assertThat(expected, equalTo(response));
        verify(userService, never()).save(any(RequestAddUserDto.class));
    }

    @SneakyThrows
    @Test
    void save_whenNotUniqueUserEmail_thenStatusConflict() {
        when(userService.save(any(RequestAddUserDto.class)))
                .thenThrow(NotUniqueFieldException.class);

        mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestUpdateUserDto))
                )
                .andExpect(status().isConflict());
        verify(userService, times(1)).save(any(RequestAddUserDto.class));
    }

    @SneakyThrows
    @Test
    void update_whenValidUser_thenStatusOkAndUserInBody() {
        when(userService.update(anyLong(), any(RequestUpdateUserDto.class)))
                .thenReturn(responseUpdatedUserDto);

        String response = mockMvc.perform(
                        patch(URL_WITH_ID, userId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestUpdateUserDto))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(objectMapper.writeValueAsString(responseUpdatedUserDto), equalTo(response));
        verify(userService, times(1)).update(anyLong(), any(RequestUpdateUserDto.class));
    }

    @SneakyThrows
    @Test
    void update_whenDuplicateUserEmail_thenStatusConflict() {
        when(userService.update(anyLong(), any(RequestUpdateUserDto.class)))
                .thenThrow(NotUniqueFieldException.class);

        mockMvc.perform(
                        patch(URL_WITH_ID, userId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestDuplicateEmailUpdateUserDto))
                )
                .andExpect(status().isConflict());
        verify(userService, times(1)).update(anyLong(), any(RequestUpdateUserDto.class));
    }

    @SneakyThrows
    @Test
    void update_whenInvalidUser_thenStatusBadRequest() {
        mockMvc.perform(
                        patch(URL_WITH_ID, userId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestInvalidUpdateUserDto))
                )
                .andExpect(status().isBadRequest());
        verify(userService, never()).update(anyLong(), any(RequestUpdateUserDto.class));
    }

    @SneakyThrows
    @Test
    void findById_whenValidUserId_thenStatusOkAndDtoInBody() {
        when(userService.findById(userId))
                .thenReturn(responseUserDto);

        String response = mockMvc.perform(
                        get(URL_WITH_ID, userId)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(objectMapper.writeValueAsString(responseUserDto), equalTo(response));
        verify(userService, times(1)).findById(anyLong());
    }

    @SneakyThrows
    @Test
    void findById_whenInvalidUserId_thenStatusNotFound() {
        when(userService.findById(invalidUserId))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        get(URL_WITH_ID, invalidUserId)
                )
                .andExpect(status().isNotFound());
        verify(userService, times(1)).findById(anyLong());
    }

    @SneakyThrows
    @Test
    void delete_whenInvalidUserId_thenStatusNotFound() {
        doThrow(UserNotFoundException.class).when(userService).deleteById(invalidUserId);

        mockMvc.perform(
                        delete(URL_WITH_ID, invalidUserId)
                )
                .andExpect(status().isNotFound());
        verify(userService, times(1)).deleteById(anyLong());
    }

    @SneakyThrows
    @Test
    void delete_whenValidUserId_thenStatusNoContent() {
        mockMvc.perform(
                        delete(URL_WITH_ID, userId)
                )
                .andExpect(status().isNoContent());
        verify(userService, times(1)).deleteById(anyLong());
    }

    @SneakyThrows
    @Test
    void findAll_whenInvoke_thenStatusOkAndDtosListInBody() {
        when(userService.findAll())
                .thenReturn(responseUserDtos);

        String response = mockMvc.perform(
                        get(URL)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(objectMapper.writeValueAsString(responseUserDtos), equalTo(response));
        verify(userService, times(1)).findAll();
    }
}