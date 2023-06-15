package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemTestGenerator;
import ru.practicum.shareit.user.maper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserTestGenerator;
import ru.practicum.shareit.user.storage.UserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemStorageTest {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Test
    void add() {
        User user = userStorage.save(UserMapper.toUser(UserTestGenerator.getUser()));
        ItemDto itemDto = ItemTestGenerator.getItem();
        itemDto.setOwner(user);
        Item item = itemStorage.save(ItemMapper.toItem(itemDto));
        assertEquals("item", item.getName());
    }

    @Test
    void update() {
        User user = userStorage.save(UserMapper.toUser(UserTestGenerator.getUser()));
        ItemDto itemDto = ItemTestGenerator.getItem();
        itemDto.setOwner(user);
        Item item = itemStorage.save(ItemMapper.toItem(itemDto));
        item.setName("updated");
        Item updatedItem = itemStorage.update(item);
        assertEquals("updated", item.getName());
    }

    @Test
    void delete() {
        User user = userStorage.save(UserMapper.toUser(UserTestGenerator.getUser()));
        ItemDto itemDto1 = ItemTestGenerator.getItem();
        itemDto1.setOwner(user);
        ItemDto itemDto2 = ItemTestGenerator.getItem();
        itemDto2.setOwner(user);
        Item item1 = itemStorage.save(ItemMapper.toItem(itemDto1));
        Item item2 = itemStorage.save(ItemMapper.toItem(itemDto2));
        for (Item item : itemStorage.findByUserId(1L)) {
            itemStorage.deleteById(item.getId());
        }
        assertEquals(0, itemStorage.findByUserId(1L).size());
    }

}