package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    @Autowired
    private Validator validator;
    private FilmController filmController;
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmStorage, filmService);
    }

    @Test
    protected void createFilm_success() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.parse("2000-01-01"), 120, null);
        ResponseEntity<Film> responseEntity = filmController.createFilm(film);
        Film createdFilm = responseEntity.getBody();

        assertNotNull(responseEntity);
        assertEquals(201, responseEntity.getStatusCodeValue());
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());
        assertEquals("Film Name", createdFilm.getName());
        assertEquals("Description", createdFilm.getDescription());
        assertEquals(LocalDate.parse("2000-01-01"), createdFilm.getReleaseDate());
        assertEquals(120, createdFilm.getDuration());
    }

    @Test
    protected void createFilm_invalidReleaseDate() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.parse("1800-01-01"), 120, null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    protected void createFilm_blankName() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }

        Film film = new Film(null, null, "Description", LocalDate.parse("2000-01-01"), 120, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        String textException = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(" "));
        assertEquals("Название не может быть пустым", textException);
    }

    @Test
    protected void updateFilm_success() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.parse("2000-01-01"), 120, null);
        ResponseEntity<Film> createResponse = filmController.createFilm(film);
        Film createdFilm = createResponse.getBody();

        Film updateInfo = new Film(createdFilm.getId(), "Updated Name", "Updated Description", LocalDate.parse("2001-01-01"), 150, null);
        ResponseEntity<Film> updateResponse = filmController.updateFilm(updateInfo);
        Film updatedFilm = updateResponse.getBody();

        assertNotNull(updateResponse);
        assertEquals(200, updateResponse.getStatusCodeValue());
        assertNotNull(updatedFilm);
        assertEquals(createdFilm.getId(), updatedFilm.getId());
        assertEquals("Updated Name", updatedFilm.getName());
        assertEquals("Updated Description", updatedFilm.getDescription());
        assertEquals(LocalDate.parse("2001-01-01"), updatedFilm.getReleaseDate());
        assertEquals(150, updatedFilm.getDuration());
    }

    @Test
    protected void updateFilm_notFound() {
        Film updateInfo = new Film(999L, "Updated Name", "Updated Description", LocalDate.parse("2001-01-01"), 150, null);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            filmController.updateFilm(updateInfo);
        });
        assertEquals("Фильм не найден", exception.getMessage());
    }

    @Test
    protected void findAllFilms() {
        Film film1 = new Film(null, "Film Name 1", "Description 1", LocalDate.parse("2000-01-01"), 120, null);
        Film film2 = new Film(null, "Film Name 2", "Description 2", LocalDate.parse("2001-01-01"), 130, null);
        filmController.createFilm(film1);
        filmController.createFilm(film2);

        ResponseEntity<Collection<Film>> responseEntity = filmController.findAll();
        Collection<Film> films = responseEntity.getBody();
        assertNotNull(films, "Коллекция фильмов не должна быть null");
        assertEquals(2, films.size(), "Количество фильмов должно быть равно 2");

    }
}