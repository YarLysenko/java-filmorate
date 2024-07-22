package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private InMemoryUserStorage userStorage;

    @BeforeEach
    protected void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    @Test
    protected void testAddFriendSuccess() {
        User user1 = new User(null, "user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(null, "user2@example.com", "user2", "User Two", LocalDate.of(1991, 2, 2), new HashSet<>());
        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);
        userService.addFriend(createdUser1.getId(), createdUser2.getId());
        assertTrue(createdUser1.getFriends().contains(createdUser2.getId()));
    }

    @Test
    protected void testAddFriendUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.addFriend(999L, 1L));
    }

    @Test
    protected void testRemoveFriendSuccess() {
        User user1 = new User(null, "user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(null, "user2@example.com", "user2", "User Two", LocalDate.of(1991, 2, 2), new HashSet<>());
        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);
        userService.addFriend(createdUser1.getId(), createdUser2.getId());
        userService.removeFriend(createdUser1.getId(), createdUser2.getId());
        assertFalse(createdUser1.getFriends().contains(createdUser2.getId()));
    }

    @Test
    protected void testRemoveFriendUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.removeFriend(999L, 1L));
    }


    @Test
    protected void testGetCommonFriends() {
        User user1 = new User(null, "user1@example.com", "user1", "User One", LocalDate.parse("1990-01-01"), new HashSet<>());
        User user2 = new User(null, "user2@example.com", "user2", "User Two", LocalDate.parse("1991-02-02"), new HashSet<>());
        User user3 = new User(null, "user3@example.com", "user3", "User Three", LocalDate.parse("1991-03-03"), new HashSet<>());
        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);
        User createdUser3 = userStorage.create(user3);
        userService.addFriend(createdUser1.getId(), createdUser3.getId());
        userService.addFriend(createdUser2.getId(), createdUser3.getId());
        List<User> commonFriends = userService.getCommonFriends(createdUser1.getId(), createdUser2.getId());
        assertEquals(1, commonFriends.size());
        assertEquals(createdUser3.getId(), commonFriends.get(0).getId());
    }
}
