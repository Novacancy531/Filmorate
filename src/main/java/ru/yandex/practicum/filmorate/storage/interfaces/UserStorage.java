package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    User getUser(Long id);

    User addUser(User user);

    void updateUser(User user);
}
