package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;
    private InMemoryUserStorage userStorage;
    private UserService userService;

    @BeforeEach
    protected void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userStorage, userService);
    }

    @Test
    protected void createUser_success() {
        User user = new User(null, "email@example.com", "login", "name", LocalDate.parse("1990-01-01"), null);
        ResponseEntity<User> responseEntity = userController.createUser(user);
        User createdUser = responseEntity.getBody();

        assertNotNull(responseEntity);
        assertEquals(201, responseEntity.getStatusCodeValue());
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("email@example.com", createdUser.getEmail());
        assertEquals("login", createdUser.getLogin());
        assertEquals("name", createdUser.getName());
        assertEquals(LocalDate.parse("1990-01-01"), createdUser.getBirthday());
    }

    @Test
    protected void createUser_invalidEmail() {
        User user = new User(null, "invalid-email", "login", "name", LocalDate.parse("1990-01-01"), null);
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            userController.createUser(user);
        });
        assertTrue(exception.getMessage().contains("Некорректный формат электронной почты"));
    }

    @Test
    protected void createUser_blankLogin() {
        User user = new User(null, "email@example.com", "", "name", LocalDate.parse("1990-01-01"), null);
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            userController.createUser(user);
        });
        assertTrue(exception.getMessage().contains("Логин не может быть пустым"));
    }

    @Test
    protected void createUser_futureBirthday() {
        User user = new User(null, "email@example.com", "login", "name", LocalDate.now().plusDays(1), null);
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            userController.createUser(user);
        });
        assertTrue(exception.getMessage().contains("Дата рождения должна быть в прошлом"));
    }

    @Test
    protected void updateUser_success() {
        User user = new User(null, "email@example.com", "login", "name", LocalDate.parse("1990-01-01"), null);
        ResponseEntity<User> createResponse = userController.createUser(user);
        User createdUser = createResponse.getBody();

        User updateInfo = new User(createdUser.getId(), "new-email@example.com", "new-login", "new-name", LocalDate.parse("1995-01-01"), null);
        ResponseEntity<User> updateResponse = userController.updateUser(updateInfo);
        User updatedUser = updateResponse.getBody();

        assertNotNull(updateResponse);
        assertEquals(200, updateResponse.getStatusCodeValue());
        assertNotNull(updatedUser);
        assertEquals(createdUser.getId(), updatedUser.getId());
        assertEquals("new-email@example.com", updatedUser.getEmail());
        assertEquals("new-login", updatedUser.getLogin());
        assertEquals("new-name", updatedUser.getName());
        assertEquals(LocalDate.parse("1995-01-01"), updatedUser.getBirthday());
    }

    @Test
    protected void updateUser_notFound() {
        User updateInfo = new User(999L, "new-email@example.com", "new-login", "new-name", LocalDate.parse("1995-01-01"), null);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.updateUser(updateInfo);
        });
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    protected void findAllUsers() {
        User user1 = new User(null, "email1@example.com", "login1", "name1", LocalDate.parse("1990-01-01"), null);
        User user2 = new User(null, "email2@example.com", "login2", "name2", LocalDate.parse("1991-01-01"), null);
        userController.createUser(user1);
        userController.createUser(user2);

        Collection<User> users = userController.findAll();
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    protected void addFriend_success() {
        // Создание пользователей с инициализацией множества друзей
        User user1 = new User(null, "email1@example.com", "login1", "name1", LocalDate.parse("1990-01-01"), new HashSet<>());
        User user2 = new User(null, "email2@example.com", "login2", "name2", LocalDate.parse("1991-01-01"), new HashSet<>());

        user1 = userController.createUser(user1).getBody();
        user2 = userController.createUser(user2).getBody();

        ResponseEntity<Void> responseEntity = userController.addFriend(user1.getId(), user2.getId());
        assertEquals(200, responseEntity.getStatusCodeValue());

        User updatedUser1 = userStorage.findById(user1.getId());
        User updatedUser2 = userStorage.findById(user2.getId());

        assertTrue(updatedUser1.getFriends().contains(user2.getId()));
        assertTrue(updatedUser2.getFriends().contains(user1.getId()));
    }

    @Test
    protected void addFriend_userNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.addFriend(999L, 1L);
        });
        assertEquals("Пользователь или друг не найден", exception.getMessage());
    }

    @Test
    protected void removeFriend_success() {
        // Создание пользователей с инициализацией множества друзей
        User user1 = new User(null, "email1@example.com", "login1", "name1", LocalDate.parse("1990-01-01"), new HashSet<>());
        User user2 = new User(null, "email2@example.com", "login2", "name2", LocalDate.parse("1991-01-01"), new HashSet<>());

        // Сохранение пользователей
        user1 = userController.createUser(user1).getBody();
        user2 = userController.createUser(user2).getBody();
        userController.addFriend(user1.getId(), user2.getId());

        // Удаление из друзей
        ResponseEntity<Void> responseEntity = userController.removeFriend(user1.getId(), user2.getId());
        assertEquals(204, responseEntity.getStatusCodeValue());

        // Получение обновленных пользователей
        User updatedUser1 = userStorage.findById(user1.getId());
        User updatedUser2 = userStorage.findById(user2.getId());

        // Проверка, что пользователи больше не друзья
        assertFalse(updatedUser1.getFriends().contains(user2.getId()));
        assertFalse(updatedUser2.getFriends().contains(user1.getId()));
    }

    @Test
    protected void removeFriend_userNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.removeFriend(999L, 1L);
        });
        assertEquals("Пользователь или друг не найден", exception.getMessage());
    }


}
