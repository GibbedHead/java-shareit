package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.dto.RequestAddItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemTestGenerator;
import ru.practicum.shareit.user.maper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserTestGenerator;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql(scripts = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void add() {
        User user = userRepository.save(userMapper.addDtoToUser(UserTestGenerator.getUser1()));
        RequestAddItemDto requestAddItemDto = ItemTestGenerator.getItem();
        requestAddItemDto.setOwnerId(user.getId());
        Item item = itemRepository.save(itemMapper.addDtoToItem(requestAddItemDto));
        assertEquals("item", item.getName());
    }

    @Test
    void update() {
        User user = userRepository.save(userMapper.addDtoToUser(UserTestGenerator.getUser1()));
        RequestAddItemDto requestAddItemDto = ItemTestGenerator.getItem();
        requestAddItemDto.setOwnerId(user.getId());
        Item item = itemRepository.save(itemMapper.addDtoToItem(requestAddItemDto));
        item.setName("updated");
        Item updatedItem = itemRepository.save(item);
        assertEquals("updated", item.getName());
    }

    @Test
    void delete() {
        User user = userRepository.save(userMapper.addDtoToUser(UserTestGenerator.getUser1()));
        RequestAddItemDto requestAddItemDto = ItemTestGenerator.getItem();
        requestAddItemDto.setOwnerId(user.getId());
        RequestAddItemDto requestAddItemDto2 = ItemTestGenerator.getItem();
        requestAddItemDto2.setOwnerId(user.getId());
        Item item1 = itemRepository.save(itemMapper.addDtoToItem(requestAddItemDto));
        Item item2 = itemRepository.save(itemMapper.addDtoToItem(requestAddItemDto2));
        for (Item item : itemRepository.findByOwnerIdOrderByIdAsc(1L)) {
            itemRepository.deleteById(item.getId());
        }
        assertEquals(0, itemRepository.findByOwnerIdOrderByIdAsc(1L).size());
    }

}