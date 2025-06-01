package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {

    User getUser(Long id);

    Map<Long, User> getUsers();

    User addUser(User user);

    void updateUser(User user);
}
