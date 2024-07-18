package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryUserStorageTest {

    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void createUser_success() {
        User user = new User(1L, "test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1), null);
        User createdUser = userStorage.create(user);
        assertNotNull(createdUser.getId());
        assertEquals("test@example.com", createdUser.getEmail());
    }

    @Test
    void createUser_invalidEmail() {
        User user = new User(1L, "invalidemail", "testlogin", "Test User", LocalDate.of(1990, 1, 1), null);
        assertThrows(ValidationException.class, () -> userStorage.create(user));
    }

    @Test
    void updateUser_success() {
        User user = new User(1L, "test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1), null);
        User createdUser = userStorage.create(user);
        createdUser.setName("Updated User");
        User updatedUser = userStorage.update(createdUser);
        assertEquals("Updated User", updatedUser.getName());
    }

    @Test
    void updateUser_notFound() {
        User user = new User(999L, "test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1), null);
        assertThrows(NotFoundException.class, () -> userStorage.update(user));
    }

    @Test
    void findAllUsers() {
        User user1 = new User(1L, "test1@example.com", "testlogin1", "Test User 1", LocalDate.of(1990, 1, 1), null);
        User user2 = new User(2L, "test2@example.com", "testlogin2", "Test User 2", LocalDate.of(1990, 2, 2), null);
        userStorage.create(user1);
        userStorage.create(user2);

        Collection<User> users = userStorage.findAll();
        assertEquals(2, users.size());
    }

    @Test
    void findUserById_success() {
        User user = new User(1L, "test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1), null);
        User createdUser = userStorage.create(user);
        User foundUser = userStorage.findById(createdUser.getId());
        assertNotNull(foundUser);
    }

    @Test
    void findUserById_notFound() {
        assertNull(userStorage.findById(999L));
    }
}


