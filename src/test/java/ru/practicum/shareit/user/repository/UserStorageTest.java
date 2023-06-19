package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserStorageTest {

//    private final UserStorage userStorage;
//
//    @Test
//    void add() {
//        User user = userStorage.save(UserMapper.toUser(UserTestGenerator.getUser()));
//        assertEquals("user", user.getName());
//    }
//
//    @Test
//    void update() {
//        User user = userStorage.save(UserMapper.toUser(UserTestGenerator.getUser()));
//        user.setName("updated");
//        User updatedUSer = userStorage.update(user);
//        assertEquals("updated", user.getName());
//    }
//
//    @Test
//    void delete() {
//        User user1 = userStorage.save(UserMapper.toUser(UserTestGenerator.getUser()));
//        User user2 = userStorage.save(UserMapper.toUser(UserTestGenerator.getUser()));
//        for (User user : userStorage.findAll()) {
//            userStorage.deleteById(user.getId());
//        }
//        assertEquals(0, userStorage.findAll().size());
//    }

}