package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestAddBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

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

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseBookingDto approve(
            @RequestHeader("X-Sharer-User-Id") Long userId,
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
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        log.info(String.format("Get booking request by id=%d", bookingId));
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseBookingDto> findByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info(String.format("Get bookings request by userId=%d and state=%s", userId, state));
        return bookingService.findByUserIdAndState(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseBookingDto> findByItemOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info(String.format("Get bookings request by ownerId=%d and state=%s", userId, state));
        return bookingService.findByItemOwner(userId, state);
    }
}
