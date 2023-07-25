package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.booking.*;
import ru.practicum.shareit.exception.model.BookingNotFoundException;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerIT {
    private static final String USER_ID_HEADER_NAME = "X-Sharer-User-Id";
    private static final String URL = "/bookings";
    private static final String URL_WITH_ID = URL + "/{bookingId}";
    private static final String URL_OWNER = URL + "/owner";
    private static final String CONTENT_TYPE = "application/json";
    private final Long userId = 1L;
    private final Long bookingId = 1L;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void save_whenValidTimeAndItemExistAndBookerNotOwnerAndItemAvailableAndUserExist_thenStatusCreatedAndDtoInResponse() {
        RequestAddBookingDto requestAddBookingDto = Instancio.create(RequestAddBookingDto.class);
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        requestAddBookingDto.setStart(now);
        requestAddBookingDto.setEnd(now.plusHours(1));
        ResponseBookingDto responseBookingDto = Instancio.create(ResponseBookingDto.class);
        when(bookingService.save(anyLong(), any(RequestAddBookingDto.class)))
                .thenReturn(responseBookingDto);

        String response = mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddBookingDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseBookingDto)));
        verify(bookingService, times(1)).save(anyLong(), any(RequestAddBookingDto.class));
    }

    @SneakyThrows
    @Test
    void save_whenValidTimeAndItemExistAndBookerNotOwnerAndItemAvailableAndUserNotExist_thenStatusNotFound() {
        RequestAddBookingDto requestAddBookingDto = Instancio.create(RequestAddBookingDto.class);
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        requestAddBookingDto.setStart(now);
        requestAddBookingDto.setEnd(now.plusHours(1));
        when(bookingService.save(anyLong(), any(RequestAddBookingDto.class)))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddBookingDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());
        verify(bookingService, times(1)).save(anyLong(), any(RequestAddBookingDto.class));
    }

    @SneakyThrows
    @Test
    void save_whenValidTimeAndItemNotExistAndBookerNotOwnerAndItemAvailableAndUserExist_thenStatusNotFound() {
        RequestAddBookingDto requestAddBookingDto = Instancio.create(RequestAddBookingDto.class);
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        requestAddBookingDto.setStart(now);
        requestAddBookingDto.setEnd(now.plusHours(1));
        when(bookingService.save(anyLong(), any(RequestAddBookingDto.class)))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddBookingDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());
        verify(bookingService, times(1)).save(anyLong(), any(RequestAddBookingDto.class));
    }

    @SneakyThrows
    @Test
    void save_whenValidTimeAndItemExistAndBookerIsOwnerAndItemAvailableAndUserExist_thenStatusNotFound() {
        RequestAddBookingDto requestAddBookingDto = Instancio.create(RequestAddBookingDto.class);
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        requestAddBookingDto.setStart(now);
        requestAddBookingDto.setEnd(now.plusHours(1));
        when(bookingService.save(anyLong(), any(RequestAddBookingDto.class)))
                .thenThrow(BookingOwnItemException.class);

        mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddBookingDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());
        verify(bookingService, times(1)).save(anyLong(), any(RequestAddBookingDto.class));
    }

    @SneakyThrows
    @Test
    void save_whenValidTimeAndItemExistAndBookerNotOwnerAndItemUnavailableAndUserExist_thenStatusBadRequest() {
        RequestAddBookingDto requestAddBookingDto = Instancio.create(RequestAddBookingDto.class);
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        requestAddBookingDto.setStart(now);
        requestAddBookingDto.setEnd(now.plusHours(1));
        when(bookingService.save(anyLong(), any(RequestAddBookingDto.class)))
                .thenThrow(BookingItemNotAvailableException.class);

        mockMvc.perform(
                        post(URL)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddBookingDto))
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isBadRequest());
        verify(bookingService, times(1)).save(anyLong(), any(RequestAddBookingDto.class));
    }

    @SneakyThrows
    @Test
    void approve_whenBookingExistUserExistUserIsItemOwnerUserIsNotBookingOwner_thenStatusOkAndDtoInResponse() {
        RequestAddBookingDto requestAddBookingDto = Instancio.create(RequestAddBookingDto.class);
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        requestAddBookingDto.setStart(now);
        requestAddBookingDto.setEnd(now.plusHours(1));
        ResponseBookingDto responseBookingDto = Instancio.create(ResponseBookingDto.class);
        responseBookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(responseBookingDto);

        String response = mockMvc.perform(
                        patch(URL_WITH_ID, bookingId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddBookingDto))
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("approved", "true")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseBookingDto)));
        verify(bookingService, times(1)).approve(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void approve_whenBookingNotExistUserExistUserIsItemOwnerUserIsNotBookingOwner_thenStatusNotFound() {
        RequestAddBookingDto requestAddBookingDto = Instancio.create(RequestAddBookingDto.class);
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        requestAddBookingDto.setStart(now);
        requestAddBookingDto.setEnd(now.plusHours(1));
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(BookingNotFoundException.class);

        mockMvc.perform(
                        patch(URL_WITH_ID, bookingId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddBookingDto))
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("approved", "true")
                )
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).approve(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void approve_whenBookingExistUserNotExistUserIsItemOwnerUserIsNotBookingOwner_thenStatusNotFound() {
        RequestAddBookingDto requestAddBookingDto = Instancio.create(RequestAddBookingDto.class);
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        requestAddBookingDto.setStart(now);
        requestAddBookingDto.setEnd(now.plusHours(1));
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        patch(URL_WITH_ID, bookingId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddBookingDto))
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("approved", "true")
                )
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).approve(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void approve_whenBookingExistUserExistUserIsNotItemOwnerUserIsNotBookingOwner_thenStatusNotFound() {
        RequestAddBookingDto requestAddBookingDto = Instancio.create(RequestAddBookingDto.class);
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        requestAddBookingDto.setStart(now);
        requestAddBookingDto.setEnd(now.plusHours(1));
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(BookingNotOwnerOperationException.class);

        mockMvc.perform(
                        patch(URL_WITH_ID, bookingId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddBookingDto))
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("approved", "true")
                )
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).approve(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void approve_whenBookingExistUserExistUserIsItemOwnerUserIsBookingOwner_thenStatusNotFound() {
        RequestAddBookingDto requestAddBookingDto = Instancio.create(RequestAddBookingDto.class);
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        requestAddBookingDto.setStart(now);
        requestAddBookingDto.setEnd(now.plusHours(1));
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(BookingApprovingOwnBookingException.class);

        mockMvc.perform(
                        patch(URL_WITH_ID, bookingId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddBookingDto))
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("approved", "true")
                )
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).approve(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void approve_whenBookingExistUserExistUserIsItemOwnerUserIsNotBookingOwnerAlreadyApproved_thenStatusBadRequest() {
        RequestAddBookingDto requestAddBookingDto = Instancio.create(RequestAddBookingDto.class);
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        requestAddBookingDto.setStart(now);
        requestAddBookingDto.setEnd(now.plusHours(1));
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(BookingAlreadyApprovedException.class);

        mockMvc.perform(
                        patch(URL_WITH_ID, bookingId)
                                .contentType(CONTENT_TYPE)
                                .content(objectMapper.writeValueAsString(requestAddBookingDto))
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("approved", "true")
                )
                .andExpect(status().isBadRequest());

        verify(bookingService, times(1)).approve(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void findById_whenUserIsBookingOrItemOwnerBookingExist_thenStatusOkAndDtoInResponse() {
        ResponseBookingDto responseBookingDto = Instancio.create(ResponseBookingDto.class);
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(responseBookingDto);

        String response = mockMvc.perform(
                        get(URL_WITH_ID, bookingId)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseBookingDto)));
        verify(bookingService, times(1)).findById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void findById_whenUserIsBookingOrItemOwnerBookingNotExist_thenStatusNotFound() {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenThrow(BookingNotFoundException.class);

        mockMvc.perform(
                        get(URL_WITH_ID, bookingId)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).findById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void findById_whenUserIsNotBookingOrItemOwnerBookingExist_thenStatusNotFound() {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenThrow(BookingNotOwnerOperationException.class);

        mockMvc.perform(
                        get(URL_WITH_ID, bookingId)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                )
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).findById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void findByUserId_whenValidStateUserExist_thenStatusOkAndDtosListInResponse() {
        List<ResponseBookingDto> responseBookingDtos = Instancio
                .ofList(ResponseBookingDto.class)
                .size(2)
                .create();
        when(bookingService.findByUserIdAndState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(responseBookingDtos);

        String response = mockMvc.perform(
                        get(URL)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseBookingDtos)));
        verify(bookingService, times(1)).findByUserIdAndState(anyLong(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findByUserId_whenValidStateUserNotExist_thenStatusNotFound() {
        when(bookingService.findByUserIdAndState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        get(URL)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).findByUserIdAndState(anyLong(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findByItemOwner_whenValidStateUserExist_thenStatusOkAndDtosListInResponse() {
        List<ResponseBookingDto> responseBookingDtos = Instancio
                .ofList(ResponseBookingDto.class)
                .size(2)
                .create();
        when(bookingService.findByItemOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(responseBookingDtos);

        String response = mockMvc.perform(
                        get(URL_OWNER)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response, equalTo(objectMapper.writeValueAsString(responseBookingDtos)));
        verify(bookingService, times(1)).findByItemOwner(anyLong(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findByItemOwner_whenValidStateUserNotExist_thenStatusNotFound() {
        when(bookingService.findByItemOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                        get(URL_OWNER)
                                .contentType(CONTENT_TYPE)
                                .header(USER_ID_HEADER_NAME, userId)
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "20")
                )
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).findByItemOwner(anyLong(), anyString(), anyInt(), anyInt());
    }
}