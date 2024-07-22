package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    private static Validator validator;

    @BeforeAll
    protected static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    protected void testValidUser() {
        User user = new User(1L, "example@example.com", "username", "User Name", LocalDate.parse("2000-01-01"),null);
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    protected void testInvalidEmail() {
        User user = new User(1L, "invalid-email", "username", "User Name", LocalDate.parse("2000-01-01"),null);
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    protected void testEmptyEmail() {
        User user = new User(1L, "", "username", "User Name", LocalDate.parse("2000-01-01"),null);
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    protected void testInvalidLogin() {
        User user = new User(1L, "example@example.com", "", "User Name", LocalDate.parse("2000-01-01"),null);
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    protected void testEmptyLogin() {
        User user = new User(1L, "example@example.com", "", "User Name", LocalDate.parse("2000-01-01"),null);
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    protected void testFutureBirthday() {
        User user = new User(1L, "example@example.com", "username", "User Name", LocalDate.now().plusDays(1),null);
        Set<jakarta.validation.ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}