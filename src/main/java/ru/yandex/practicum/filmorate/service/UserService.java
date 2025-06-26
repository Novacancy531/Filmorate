package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.dal.mapper.UserMapper;
import ru.yandex.practicum.filmorate.storage.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;


@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;


    public UserDto addUser(final UserDto user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пользователь не заполнил имя, в поле имя установлен логин пользователя: {}.", user.getName());
        }

        log.info("Пользователь {} создан.", user.getName());
        return UserMapper.toDto(userStorage.addUser(UserMapper.fromDto(user)));
    }

    public Collection<UserDto> getAllUsers() {
        log.info("Отправлен список всех пользователей.");
        return userStorage.getUsers().values().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public UserDto updateUser(final UserDto user) {
        checkUserExists(user.getId());

        log.info("Обновляется информация о пользователе с id: {}.", user.getId());
        return UserMapper.toDto(userStorage.updateUser(UserMapper.fromDto(user)));
    }

    public void addFriend(final long id, final long friendId) {
        checkUserExists(id);
        checkUserExists(friendId);

        if (id == friendId) {
            log.warn("Пользователь попытался добавить в друзья сам себя.");
            throw new ConditionsNotMetException("Нельзя добавить в друзья самого себя.");
        }
        if (userStorage.checkUserFriend(id, friendId)) {
            log.warn("Пользователь уже в списке друзей.");
            throw new ConditionsNotMetException("Пользователь уже в списке друзей.");
        }

        userStorage.addFriend(id, friendId);
            log.info("Пользователь с id {} добавил пользователя {} в друзья.", id, friendId);

    }

    public void deleteFriend(final long id, final long friendId) {
        checkUserExists(id);
        checkUserExists(friendId);

        userStorage.deleteFriend(id, friendId);
        log.info("Пользователь с id: {} удален из списка друзей пользователя с id: {}.", friendId, id);
    }

    public Collection<UserDto> userFriendsList(final long id) {
        checkUserExists(id);

        log.info("Отправлен список друзей пользователя с id: {}.", id);
        return userStorage.getUserFriends(id).stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public Collection<UserDto> commonFriends(final long id, final long friendId) {
        checkUserExists(id);
        checkUserExists(friendId);

        log.info("Отправляется список общих друзей пользователей с id {} и {}.", id, friendId);
        return userStorage.getUserFriends(id).stream()
                        .filter(user -> userStorage.getUserFriends(friendId).contains(user))
                                .map(UserMapper::toDto).toList();
    }

    public void checkUserExists(final long id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден.");
        }
    }
}
