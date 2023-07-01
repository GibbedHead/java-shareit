package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;

    private final Long userId = 1L;
    private final Long bookingId = 1L;
    private final Long itemId = 1L;
    private final LocalDateTime now = LocalDateTime.now();
    private final ResponseBookingDto responseBookingDto = new ResponseBookingDto(
            bookingId,
            now,
            now,
            BookingStatus.WAITING,
            null,
            null
    );
    private final ResponseBookingDto approvedResponseBookingDto = new ResponseBookingDto(
            bookingId,
            now,
            now,
            BookingStatus.APPROVED,
            null,
            null
    );
    private final List<ResponseBookingDto> responseBookingDtos = List.of(responseBookingDto);

    @Test
    void save_whenInvoke_thenReturnResponseDto() {
        RequestAddBookingDto requestAddBookingDto = new RequestAddBookingDto(
                itemId,
                now,
                now
        );

        when(bookingService.save(userId, requestAddBookingDto))
                .thenReturn(responseBookingDto);

        ResponseBookingDto responseBookingDto1 = bookingController.save(userId, requestAddBookingDto);

        assertThat(1L, equalTo(responseBookingDto1.getId()));
    }

    @Test
    void approve_whenInvoke_thenReturnResponseDto() {
        Boolean approved = true;

        when(bookingService.approve(userId, bookingId, approved))
                .thenReturn(approvedResponseBookingDto);

        ResponseBookingDto bookingDto = bookingController.approve(userId, bookingId, approved);

        assertThat(BookingStatus.APPROVED, equalTo(bookingDto.getStatus()));
    }

    @Test
    void findById_whenInvoke_thenReturnResponseDto() {
        when(bookingService.findById(userId, bookingId))
                .thenReturn(responseBookingDto);

        ResponseBookingDto bookingDto = bookingController.findById(userId, bookingId);

        assertThat(1L, equalTo(bookingDto.getId()));
    }

    @Test
    void findByUserId_whenInvoke_thenReturnResponseDtoList() {
        when(bookingService.findByUserIdAndState(userId, "ALL", 0, 20))
                .thenReturn(responseBookingDtos);

        List<ResponseBookingDto> bookingDtos = bookingController.findByUserId(userId, "ALL", 0, 20);

        assertThat(1L, equalTo(bookingDtos.get(0).getId()));
    }

    @Test
    void findByItemOwner_whenInvoke_thenReturnResponseDtoList() {
        when(bookingService.findByItemOwner(userId, "ALL", 0, 20))
                .thenReturn(responseBookingDtos);

        List<ResponseBookingDto> bookingDtos = bookingController.findByItemOwner(userId, "ALL", 0, 20);

        assertThat(1L, equalTo(bookingDtos.get(0).getId()));
    }
}