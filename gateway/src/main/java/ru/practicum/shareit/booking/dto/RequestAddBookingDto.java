package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestAddBookingDto {
    @NotNull(message = "Item id not be empty")
    Long itemId;
    @FutureOrPresent(message = "Start date must be present or in future")
    @NotNull(message = "Start date must not be empty")
    LocalDateTime start;
    @Future(message = "End date must not be in future")
    @NotNull(message = "End date must not be empty")
    LocalDateTime end;
}
