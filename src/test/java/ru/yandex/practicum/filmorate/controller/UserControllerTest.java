package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private UserController userController;
    private InMemoryUserStorage userStorage;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userStorage, userService);
    }

    @Test
    void createUserWithEmptyEmail() {
        User user = new User(1L, "", "testlogin", "Test User", LocalDate.now(), null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Неверный формат электронной почты", exception.getMessage());
    }

    @Test
    void createUserWithInvalidEmail() {
        User user = new User(1L, "invalid.email", "testlogin", "Test User", LocalDate.now(), null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Неверный формат электронной почты", exception.getMessage());
    }

    @Test
    void createUserWithEmptyLogin() {
        User user = new User(null, "test@example.com", "", "Test User", LocalDate.now(), null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage());
    }

    @Test
    void createUserWithFutureBirthday() {
        User user = new User(null, "test@example.com", "testlogin", "Test User", LocalDate.now().plusDays(1), null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}