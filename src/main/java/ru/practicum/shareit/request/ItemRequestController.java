package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestAddItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private static final String USER_ID_HEADER_NAME = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseItemRequestDto save(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @Valid @RequestBody RequestAddItemRequestDto requestAddItemRequestDto
    ) {
        log.info(String.format("Add item request request: %s", requestAddItemRequestDto));
        return itemRequestService.save(userId, requestAddItemRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseItemRequestWithItemsDto> findByUserId(@RequestHeader(USER_ID_HEADER_NAME) Long userId) {
        log.info(String.format("Get item requests request by userId#%d ", userId));
        return itemRequestService.findByUserId(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseItemRequestWithItemsDto> findAllNotOwned(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "From parameter must be greater than 0")
            Integer from,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "Size parameter must be greater than 1")
            Integer size
    ) {
        log.info(String.format("Get all item requests request from: %d; size: %d", from, size));
        return itemRequestService.findAllNotOwned(userId, from, size);
    }
}
