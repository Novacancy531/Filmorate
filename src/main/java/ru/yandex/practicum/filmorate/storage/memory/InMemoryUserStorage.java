package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private final HashMap<Long, Set<Long>> userFriends = new HashMap<>();

    private long currentLastId = 0L;

    @Override
    public User addUser(User user) {
        user.setId(getNewId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(long id, long friendId) {
        userFriends.putIfAbsent(id, new HashSet<>());
        userFriends.get(id).add(friendId);
    }

    @Override
    public List<User> getUserFriends(long id) {
        if (!userFriends.containsKey(id)) {
            return Collections.emptyList();
        }
        Set<Long> friendsIds = userFriends.get(id);
        return friendsIds.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }

    @Override
    public Map<Long, User> getUsers() {
        return new HashMap<>(users);
    }

    @Override
    public User updateUser(User user) {
        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        if (userFriends.containsKey(id)) {
            userFriends.get(id).remove(friendId);
        }
    }

    @Override
    public boolean checkUserFriend(long id, long friendId) {
        return userFriends.containsKey(id) && userFriends.get(id).contains(friendId);
    }

    private Long getNewId() {
        return ++currentLastId;
    }
}
