package ru.yandex.practicum.filmorate.storage.dal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserRepository.class})
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testAddAndGetUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User savedUser = userRepository.addUser(user);
        assertNotNull(savedUser.getId(), "ID должен быть сгенерирован");

        User userFromDb = userRepository.getUser(savedUser.getId());
        assertEquals("testuser", userFromDb.getLogin());
        assertEquals("test@example.com", userFromDb.getEmail());
        assertEquals(LocalDate.of(1990, 1, 1), userFromDb.getBirthday());
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("update@example.com");
        user.setLogin("updateuser");
        user.setName("Update User");
        user.setBirthday(LocalDate.of(1995, 5, 5));

        User savedUser = userRepository.addUser(user);

        savedUser.setEmail("updated@example.com");
        savedUser.setName("Updated Name");
        userRepository.updateUser(savedUser);

        User updatedUser = userRepository.getUser(savedUser.getId());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void testGetUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1980, 1, 1));
        userRepository.addUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1985, 2, 2));
        userRepository.addUser(user2);

        Map<Long, User> allUsers = userRepository.getUsers();
        assertTrue(allUsers.size() >= 2, "В базе должно быть не менее 2 пользователей");
        assertTrue(allUsers.values().stream().anyMatch(u -> "user1".equals(u.getLogin())));
        assertTrue(allUsers.values().stream().anyMatch(u -> "user2".equals(u.getLogin())));
    }

    @Test
    void testAddAndGetFriends() {
        User userA = new User();
        userA.setEmail("a@example.com");
        userA.setLogin("userA");
        userA.setName("User A");
        userA.setBirthday(LocalDate.of(1990, 3, 3));
        userA = userRepository.addUser(userA);

        User userB = new User();
        userB.setEmail("b@example.com");
        userB.setLogin("userB");
        userB.setName("User B");
        userB.setBirthday(LocalDate.of(1992, 4, 4));
        userB = userRepository.addUser(userB);

        userRepository.addFriend(userA.getId(), userB.getId());

        List<User> friendsOfA = userRepository.getUserFriends(userA.getId());
        User finalUserB = userB;
        assertTrue(friendsOfA.stream().anyMatch(u -> u.getId().equals(finalUserB.getId())));
    }
}
