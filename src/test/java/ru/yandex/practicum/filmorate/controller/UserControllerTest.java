package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;
    private Map<Long, User> users;

    @BeforeEach
    protected void setUp() {
        users = new HashMap<>();
        userController = new UserController();
    }

    @Test
    protected void testCreateUserWithEmptyEmail() {
        User user = new User(null, "", "testlogin", "Test User", LocalDate.now());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Неверный формат электронной почты", exception.getMessage());
    }

    @Test
    protected void testCreateUserWithInvalidEmail() {
        User user = new User(null, "invalid.email", "testlogin", "Test User", LocalDate.now());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Неверный формат электронной почты", exception.getMessage());
    }

    @Test
    protected void testCreateUserWithEmptyLogin() {
        User user = new User(null, "test@example.com", "", "Test User", LocalDate.now());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage());
    }

    @Test
    protected void testCreateUserWithFutureBirthday() {
        User user = new User(null, "test@example.com", "testlogin", "Test User", LocalDate.now().plusDays(1));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    protected void testUpdateUserInvalidId() {
        User userWithInvalidId = new User(0L, "test@example.com", "testlogin", "Test User", LocalDate.now());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(userWithInvalidId));
        assertEquals("Id должен быть указан", exception.getMessage());
    }

    @Test
    protected void testUpdateUserSuccess() {
        User existingUser = new User(1L, "test@example.com", "testlogin", "Test User", LocalDate.now());
        userController.createUser(existingUser);
        User updatedUser = new User(1L, "updated@example.com", "updatedlogin", "Updated User", LocalDate.now().minusDays(1));

        User returnedUser = userController.updateUser(updatedUser);
        assertEquals(updatedUser.getId(), returnedUser.getId());
        assertEquals("updated@example.com", returnedUser.getEmail());
        assertEquals("updatedlogin", returnedUser.getLogin());
        assertEquals("Updated User", returnedUser.getName());
        assertEquals(LocalDate.now().minusDays(1), returnedUser.getBirthday());
    }

    @Test
    protected void testUpdateUserNotFound() {
        User user = new User(1L, "updated@example.com", "updatedlogin", "Updated User", LocalDate.now().minusDays(1));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userController.updateUser(user));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    protected void testFindAllUsersEmpty() {
        Collection<User> allUsers = userController.findAll();
        assertEquals(0, allUsers.size());
    }

    @Test
    protected void testFindAllUsers() {
        User user1 = new User(1L, "test1@example.com", "testlogin1", "Test User 1", LocalDate.now());
        User user2 = new User(2L, "test2@example.com", "testlogin2", "Test User 2", LocalDate.now());
        userController.createUser(user1);
        userController.createUser(user2);

        Collection<User> allUsers = userController.findAll();
        assertEquals(2, allUsers.size());
        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }
}
