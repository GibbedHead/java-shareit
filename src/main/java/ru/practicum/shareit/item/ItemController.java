package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.RequestAddItemDto;
import ru.practicum.shareit.item.dto.RequestUpdateItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseItemDto save(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody RequestAddItemDto itemDto
    ) {
        log.info("Add item request: " + itemDto);
        return itemService.save(userId, itemDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseItemDto update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody RequestUpdateItemDto itemUpdateDto
    ) {
        log.info("Update item id " + id + ". Data: " + itemUpdateDto);
        return itemService.update(userId, id, itemUpdateDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseItemDto findById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id) {
        log.info("Get item request id " + id);
        return itemService.findById(userId, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseItemDto> findByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get items request by userId " + userId);
        return itemService.findByUserId(userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Delete item request id " + id);
        itemService.deleteById(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseItemDto> findByNameOrDescription(@RequestParam String text) {
        log.info("Get items request by text '" + text + "'");
        return itemService.findByNameOrDescription(text);
    }
}
