package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        try {
            validateUser(user);

            user.setId(getNextId());
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Создан новый пользователь: {}", user);
            return user;
        } catch (ValidationException e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        try {
            if (user.getId() == 0) {
                throw new ValidationException("Id должен быть указан");
            }
            User existingUser = users.get(user.getId());
            if (existingUser == null) {
                throw new NotFoundException("Пользователь не найден");
            }

            validateUser(user);

            existingUser.setEmail(user.getEmail());
            existingUser.setLogin(user.getLogin());
            existingUser.setName(user.getName());
            existingUser.setBirthday(user.getBirthday());
            log.info("Обновлен пользователь: {}", existingUser);
            return existingUser;
        } catch (ValidationException | NotFoundException e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            throw e;
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Неверный формат электронной почты");
        }
        if (user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}