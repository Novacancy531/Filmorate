package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    User addUser(User user);

    void addFriend(long id,long friendId);

    List<User> getUserFriends(long id);

    User getUser(long id);

    Map<Long, User> getUsers();

    User updateUser(User user);

    void deleteFriend(long id, long friendId);

    boolean checkUserFriend(long id, long friendsId);
}
