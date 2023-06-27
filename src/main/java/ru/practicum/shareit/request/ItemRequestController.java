package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestAddItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private static final String USER_ID_HEADER_NAME = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseItemRequestDto save(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @Valid @RequestBody RequestAddItemRequestDto requestAddItemRequestDto
    ) {
        log.info("Add item request request: " + requestAddItemRequestDto);
        return itemRequestService.save(userId, requestAddItemRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseItemRequestWithItemsDto> findByUserId(@RequestHeader(USER_ID_HEADER_NAME) Long userId) {
        log.info("Get item requests request by userId " + userId);
        return itemRequestService.findByUserId(userId);
    }
}
