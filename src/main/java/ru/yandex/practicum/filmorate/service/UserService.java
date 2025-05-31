package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage userStorage;


    public User createUser(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пользователь не заполнил имя, в поле имя установлен логин пользователя: {}.", user.getName());
        }

        log.info("Пользователь {} создан.", user.getName());
        return userStorage.addUser(user);
    }

    public Collection<User> getAllUsers() {
        log.info("Отправлен список всех пользователей.");
        return userStorage.getUsers().values();
    }

    public User updateUser(final User user) {
        ifContains(user.getId());

        userStorage.updateUser(user);
        log.info("Информация о пользователе с id: {} обновлена.", user.getId());
        return userStorage.getUser(user.getId());
    }

    public void addFriend(final long id, final long friendId) {
        ifContains(id);
        ifContains(friendId);

        log.info("Пользователи с id {} и {} теперь друзья.", id, friendId);
        userStorage.getUser(id).getFriendList().add(friendId);
        userStorage.getUser(friendId).getFriendList().add(id);
    }

    public void deleteFriend(final long id, final long friendId) {
        ifContains(id);
        ifContains(friendId);

        log.info("Пользователь с id: {} удален из списка друзей пользователя с id: {}.",friendId, id);
        userStorage.getUser(id).getFriendList().remove(friendId);
        userStorage.getUser(friendId).getFriendList().remove(id);
    }

    public Collection<User> userFriendsList(final long id) {
        ifContains(id);

        log.info("Отправлен список друзей пользователя с id: {}.", id);
        return userStorage.getUser(id).getFriendList().stream()
                .map(userId -> userStorage.getUsers().get(userId))
                .toList();
    }

    public Collection<User> commonFriends(final long id, final long otherId) {
        ifContains(id);
        ifContains(otherId);

        Set<Long> friends = new HashSet<>(userStorage.getUser(otherId).getFriendList());

        log.info("Отправлен список общих друзей пользователей с id {} и {}.", id, otherId);
        return userStorage.getUser(id).getFriendList().stream()
                .filter(friends::contains)
                .map(userId -> userStorage.getUsers().get(userId))
                .toList();
    }

    public void ifContains(final long id) {
        if (!userStorage.getUsers().containsKey(id)) {
            log.info("Запрошенный пользователь с id: {} не найден.", id);
            throw new ConditionsNotMetException("Пользователь с id: " + id + " не найден.");
        }
    }
}
