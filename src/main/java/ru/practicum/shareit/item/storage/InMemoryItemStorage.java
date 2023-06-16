package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id;

    @Override
    public Item save(Item item) {
        Long id = getId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findByUserId(Long userId) {
        return items.values().stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Item item) {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
            return item;
        }
        throw new ItemNotFoundException(String.format("Item id=%d not found", item.getId()));
    }

    @Override
    public void deleteById(Long id) {
        if (items.containsKey(id)) {
            items.remove(id);
            return;
        }
        throw new ItemNotFoundException(String.format("Item id=%d not found", id));
    }

    @Override
    public List<Item> findByNameOrDescription(String text) {
        return items.values().stream()
                .filter(item -> hasTextAndIsAvailable(item, text))
                .collect(Collectors.toList());
    }

    private boolean hasTextAndIsAvailable(Item item, String text) {
        return (item.getName().toLowerCase().contains(text.toLowerCase())
                ||
                item.getDescription().toLowerCase().contains(text.toLowerCase()))
                &&
                item.getAvailable();
    }

    private long getId() {
        return ++id;
    }
}
