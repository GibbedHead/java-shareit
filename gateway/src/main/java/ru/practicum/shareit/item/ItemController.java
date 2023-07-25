package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.RequestAddCommentDto;
import ru.practicum.shareit.item.dto.RequestAddItemDto;
import ru.practicum.shareit.item.dto.RequestUpdateItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_ID_HEADER_NAME = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> save(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @Valid @RequestBody RequestAddItemDto itemDto
    ) {
        log.info("Add item request: " + itemDto);
        return itemClient.save(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @PathVariable Long id,
            @Valid @RequestBody RequestUpdateItemDto updateItemDto
    ) {
        log.info("Update item id " + id + ". Data: " + updateItemDto);
        return itemClient.update(userId, id, updateItemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @PathVariable Long id) {
        log.info("Get item request id " + id);
        return itemClient.findById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "From parameter must be greater or equal 0")
            Integer from,
            @RequestParam(defaultValue = "20")
            @Positive(message = "Size parameter must be positive")
            Integer size
    ) {
        log.info("Get items request by userId " + userId);
        return itemClient.findByUserId(userId, from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Delete item request id " + id);
        itemClient.deleteById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByNameOrDescription(
            @RequestParam String text,
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "From parameter must be greater or equal 0")
            Integer from,
            @RequestParam(defaultValue = "20")
            @Positive(message = "Size parameter must be positive")
            Integer size) {
        log.info("Get items request by text '" + text + "'");
        return itemClient.findByNameOrDescription(text, from, size);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> saveComment(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId,
            @PathVariable Long id,
            @Valid @RequestBody RequestAddCommentDto addCommentDto
    ) {
        log.info(String.format("Add comment request userId=%d, itemId=%d, text='%s'", userId, id, addCommentDto));
        return itemClient.saveComment(userId, id, addCommentDto);
    }
}
