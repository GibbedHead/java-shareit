package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id;

    @Override
    public Item save(Item item) {
        return null;
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.empty();
    }

    @Override
    public List<Item> findAll() {
        return null;
    }

    @Override
    public Item update(Item item) {
        return null;
    }

    @Override
    public void deleteById(long id) {

    }

    private long getId() {
        return ++id;
    }
}
