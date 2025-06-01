package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users;
    private long currentLastId;


    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public Map<Long, User> getUsers() {
        return new HashMap<>(users);
    }

    @Override
    public User addUser(User user) {
        user.setId(getNewId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void updateUser(User user) {
        users.replace(user.getId(), user);

    }

    private Long getNewId() {
        return ++currentLastId;
    }
}
