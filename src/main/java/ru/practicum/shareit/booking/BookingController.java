package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private static final String USER_ID_HEADER_NAME = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseBookingDto save(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @Valid @RequestBody RequestAddBookingDto requestAddBookingDto
    ) {
        log.info("Add booking request: " + requestAddBookingDto);
        return bookingService.save(userId, requestAddBookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBookingDto approve(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved
    ) {
        log.info(String.format(
                "Approve booking request from UserId=%d, for BookingId=%d, setting Approve=%s",
                userId,
                bookingId,
                approved
        ));
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBookingDto findById(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @PathVariable Long bookingId
    ) {
        log.info(String.format("Get booking request by id=%d", bookingId));
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseBookingDto> findByUserId(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "From parameter must be greater or equal 0")
            Integer from,
            @RequestParam(defaultValue = "20")
            @Positive(message = "Size parameter must be positive")
            Integer size
    ) {
        log.info(String.format("Get bookings request by userId=%d and state=%s", userId, state));
        return bookingService.findByUserIdAndState(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseBookingDto> findByItemOwner(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "From parameter must be greater or equal 0")
            Integer from,
            @RequestParam(defaultValue = "20")
            @Positive(message = "Size parameter must be positive")
            Integer size
    ) {
        log.info(String.format("Get bookings request by ownerId=%d and state=%s", userId, state));
        return bookingService.findByItemOwner(userId, state, from, size);
    }
}
