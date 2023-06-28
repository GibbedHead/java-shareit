package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_ID_HEADER_NAME = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseItemDto save(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @Valid @RequestBody RequestAddItemDto itemDto
    ) {
        log.info("Add item request: " + itemDto);
        return itemService.save(userId, itemDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseItemDto update(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @PathVariable Long id,
            @Valid @RequestBody RequestUpdateItemDto itemUpdateDto
    ) {
        log.info("Update item id " + id + ". Data: " + itemUpdateDto);
        return itemService.update(userId, id, itemUpdateDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseItemWithCommentsDto findById(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @PathVariable Long id) {
        log.info("Get item request id " + id);
        return itemService.findById(userId, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseItemWithCommentsDto> findByUserId(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "From parameter must be greater or equal 0")
            Integer from,
            @RequestParam(defaultValue = "20")
            @Positive(message = "Size parameter must be positive")
            Integer size
    ) {
        log.info("Get items request by userId " + userId);
        return itemService.findByUserId(userId, from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Delete item request id " + id);
        itemService.deleteById(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseItemDto> findByNameOrDescription(
            @RequestParam String text,
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "From parameter must be greater or equal 0")
            Integer from,
            @RequestParam(defaultValue = "20")
            @Positive(message = "Size parameter must be positive")
            Integer size) {
        log.info("Get items request by text '" + text + "'");
        return itemService.findByNameOrDescription(text, from, size);
    }

    @PostMapping("/{id}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseCommentDto saveComment(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @PathVariable Long id,
            @Valid @RequestBody RequestAddCommentDto addCommentDto
    ) {
        log.info(String.format("Add comment request userId=%d, itemId=%d, text='%s'", userId, id, addCommentDto));
        return itemService.saveComment(userId, id, addCommentDto);
    }
}
