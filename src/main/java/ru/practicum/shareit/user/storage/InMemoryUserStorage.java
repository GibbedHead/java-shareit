package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public User save(User user) {
        long id = getId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        }
        throw new UserNotFoundException(String.format("User id=%d not found", user.getId()));
    }

    @Override
    public void deleteById(long id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return;
        }
        throw new UserNotFoundException(String.format("User id=%d not found", id));
    }

    private long getId() {
        return ++id;
    }

    public boolean isNotUniqueEmail(String email, Long id) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email) && !Objects.equals(user.getId(), id)) {
                return true;
            }
        }
        return false;
    }
}
