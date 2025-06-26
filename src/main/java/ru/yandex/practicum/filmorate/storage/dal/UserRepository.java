package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdGenerationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class UserRepository implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String ADD_USER = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String ADD_FRIEND = "MERGE INTO user_friends (user_id, friend_id, is_confirmed) " +
            "KEY (user_id, friend_id) VALUES (?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?," +
            "birthday = ? WHERE user_id = ?";
    private static final String GET_USER = "SELECT * FROM users WHERE user_id = ?";
    private static final String GET_ALL_USER = "SELECT * FROM users";
    private static final String GET_FRIENDS_LIST = "SELECT friend_id FROM user_friends WHERE user_id = ?";
    private static final String DELETE_FRIEND = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
    private static final String CANCEL_FRIENDSHIP_CONFIRMATION = "UPDATE user_friends SET is_confirmed = false " +
            "WHERE user_id = ? AND friend_id = ?";

    @Override
    public User addUser(final User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    ADD_USER, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            user.setId(key.longValue());
        } else {
            throw new IdGenerationException("Пользователю не присвоен id.");
        }
        return user;
    }

    public void addFriend(final long id, final long friendId) {
        List<Long> friendList = jdbcTemplate.queryForList(GET_FRIENDS_LIST, Long.class, friendId);
        boolean isConfirmed = friendList.contains(id);
        jdbcTemplate.update(ADD_FRIEND, id, friendId, isConfirmed);

        if (isConfirmed) {
            jdbcTemplate.update(ADD_FRIEND, friendId, id, true);
        }
    }

    @Override
    public User updateUser(final User user) {
        jdbcTemplate.update(UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public User getUser(final long id) {
        return jdbcTemplate.queryForObject(GET_USER, this::mapRowToUser, id);
    }

    @Override
    public Map<Long, User> getUsers() {
        List<User> users = jdbcTemplate.query(GET_ALL_USER, this::mapRowToUser);
        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    @Override
    public List<User> getUserFriends(final long id) {
        List<Long> friendList = jdbcTemplate.queryForList(GET_FRIENDS_LIST, Long.class, id);
        List<User> userList = new ArrayList<>();


        for (Long userId : friendList) {
            userList.add(getUser(userId));
        }
        return userList;
    }

    @Override
    public void deleteFriend(final long id, final long friendId) {
        jdbcTemplate.update(DELETE_FRIEND, id, friendId);
        jdbcTemplate.update(CANCEL_FRIENDSHIP_CONFIRMATION, friendId, id);
    }

    @Override
    public boolean checkUserFriend(final long id, final long friendsId) {
        return jdbcTemplate.queryForList(GET_FRIENDS_LIST, Long.class, id).contains(friendsId);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getObject("birthday", LocalDate.class));
        return user;
    }
}
