package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.maper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserTestGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql(scripts = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {

    private final UserRepository userRepository;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void add() {
        User user = userRepository.save(userMapper.addDtoToUser(UserTestGenerator.getUser1()));
        assertEquals("user1", user.getName());
    }

    @Test
    void update() {
        User user = userRepository.save(userMapper.addDtoToUser(UserTestGenerator.getUser1()));
        user.setName("updated");
        User updatedUSer = userRepository.save(user);
        assertEquals("updated", user.getName());
    }

    @Test
    void delete() {
        User user1 = userRepository.save(userMapper.addDtoToUser(UserTestGenerator.getUser1()));
        User user2 = userRepository.save(userMapper.addDtoToUser(UserTestGenerator.getUser2()));
        for (User user : userRepository.findAll()) {
            userRepository.deleteById(user.getId());
        }
        assertEquals(0, userRepository.findAll().size());
    }

}