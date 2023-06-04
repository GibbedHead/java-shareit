package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User save(User user);

    Optional<User> findById(long id);

    List<User> findAll();

    User update(User user);

    void deleteById(long id);

    boolean isNotUniqueEmail(String email, Long id);

}
