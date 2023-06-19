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
        RequestAddItemDto RequestAddItemDto = ItemTestGenerator.getItem();
        RequestAddItemDto.setOwnerId(user.getId());
        Item item = itemRepository.save(itemMapper.addDtoToItem(RequestAddItemDto));
        assertEquals("item", item.getName());
    }

    @Test
    void update() {
        User user = userRepository.save(userMapper.addDtoToUser(UserTestGenerator.getUser1()));
        RequestAddItemDto RequestAddItemDto = ItemTestGenerator.getItem();
        RequestAddItemDto.setOwnerId(user.getId());
        Item item = itemRepository.save(itemMapper.addDtoToItem(RequestAddItemDto));
        item.setName("updated");
        Item updatedItem = itemRepository.save(item);
        assertEquals("updated", item.getName());
    }

    @Test
    void delete() {
        User user = userRepository.save(userMapper.addDtoToUser(UserTestGenerator.getUser1()));
        RequestAddItemDto RequestAddItemDto1 = ItemTestGenerator.getItem();
        RequestAddItemDto1.setOwnerId(user.getId());
        RequestAddItemDto RequestAddItemDto2 = ItemTestGenerator.getItem();
        RequestAddItemDto2.setOwnerId(user.getId());
        Item item1 = itemRepository.save(itemMapper.addDtoToItem(RequestAddItemDto1));
        Item item2 = itemRepository.save(itemMapper.addDtoToItem(RequestAddItemDto2));
        for (Item item : itemRepository.findByOwnerIdOrderByIdAsc(1L)) {
            itemRepository.deleteById(item.getId());
        }
        assertEquals(0, itemRepository.findByOwnerIdOrderByIdAsc(1L).size());
    }

}