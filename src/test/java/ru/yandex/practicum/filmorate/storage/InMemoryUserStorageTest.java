package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryUserStorageTest {

    private InMemoryUserStorage userStorage;

    @BeforeEach
    protected void setUp() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    protected void createUser_success() {
        User user = new User(1L, "test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1), null);
        User createdUser = userStorage.create(user);
        assertNotNull(createdUser.getId());
        assertEquals("test@example.com", createdUser.getEmail());
    }


    @Test
    protected void updateUser_success() {
        User user = new User(1L, "test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1), null);
        User createdUser = userStorage.create(user);
        createdUser.setName("Updated User");
        User updatedUser = userStorage.update(createdUser);
        assertEquals("Updated User", updatedUser.getName());
    }

    @Test
    protected void updateUser_notFound() {
        User user = new User(999L, "test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1), null);
        assertThrows(NotFoundException.class, () -> userStorage.update(user));
    }

    @Test
    protected void findAllUsers() {
        User user1 = new User(1L, "test1@example.com", "testlogin1", "Test User 1", LocalDate.of(1990, 1, 1), null);
        User user2 = new User(2L, "test2@example.com", "testlogin2", "Test User 2", LocalDate.of(1990, 2, 2), null);
        userStorage.create(user1);
        userStorage.create(user2);

        Collection<User> users = userStorage.findAll();
        assertEquals(2, users.size());
    }

    @Test
    protected void findUserById_success() {
        User user = new User(1L, "test@example.com", "testlogin", "Test User", LocalDate.of(1990, 1, 1), null);
        User createdUser = userStorage.create(user);
        User foundUser = userStorage.findById(createdUser.getId());
        assertNotNull(foundUser);
    }

    @Test
    protected void findUserById_notFound() {
        assertNull(userStorage.findById(999L));
    }
}


