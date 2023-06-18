package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseBookingDto save(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody RequestAddBookingDto requestAddBookingDto
    ) {
        log.info("Add booking request: " + requestAddBookingDto);
        return bookingService.save(userId, requestAddBookingDto);
    }
}
