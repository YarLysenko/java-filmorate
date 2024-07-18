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

public class FilmTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidFilm() {
        Film film = new Film(1L, "Inception", "A mind-bending thriller", LocalDate.parse("2010-07-16"), 148, null);
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidFilmName() {
        Film film = new Film(1L, "", "A mind-bending thriller", LocalDate.parse("2010-07-16"), 148, null);
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidFilmDescription() {
        Film film = new Film(1L, "Inception", "A".repeat(201), LocalDate.parse("2010-07-16"), 148, null);
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testInvalidFilmDuration() {
        Film film = new Film(1L, "Inception", "A mind-bending thriller", LocalDate.parse("2010-07-16"), -10, null);
        Set<jakarta.validation.ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}
