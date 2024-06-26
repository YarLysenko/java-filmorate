package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    protected void setUp() {
        filmController = new FilmController();
    }

    @Test
    protected void createFilm_success() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.parse("2000-01-01"), 120);
        Film createdFilm = filmController.createFilm(film);
        assertNotNull(createdFilm.getId());
        assertEquals("Film Name", createdFilm.getName());
        assertEquals("Description", createdFilm.getDescription());
        assertEquals(LocalDate.parse("2000-01-01"), createdFilm.getReleaseDate());
        assertEquals(120, createdFilm.getDuration());
    }

    @Test
    protected void createFilm_invalidReleaseDate() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.parse("1800-01-01"), 120);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    protected void createFilm_blankName() {
        Film film = new Film(null, "", "Description", LocalDate.parse("2000-01-01"), 120);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    protected void updateFilm_success() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.parse("2000-01-01"), 120);
        Film createdFilm = filmController.createFilm(film);

        Film updateInfo = new Film(createdFilm.getId(), "Updated Name", "Updated Description", LocalDate.parse("2001-01-01"), 150);
        Film updatedFilm = filmController.updateFilm(updateInfo);

        assertEquals(createdFilm.getId(), updatedFilm.getId());
        assertEquals("Updated Name", updatedFilm.getName());
        assertEquals("Updated Description", updatedFilm.getDescription());
        assertEquals(LocalDate.parse("2001-01-01"), updatedFilm.getReleaseDate());
        assertEquals(150, updatedFilm.getDuration());
    }

    @Test
    protected void updateFilm_notFound() {
        Film updateInfo = new Film(999L, "Updated Name", "Updated Description", LocalDate.parse("2001-01-01"), 150);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            filmController.updateFilm(updateInfo);
        });
        assertEquals("Фильм не найден", exception.getMessage());
    }

    @Test
    protected void updateFilm_noId() {
        Film updateInfo = new Film(0L, "Updated Name", "Updated Description", LocalDate.parse("2001-01-01"), 150);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(updateInfo);
        });
        assertEquals("Id должен быть указан", exception.getMessage());
    }

    @Test
    protected void findAllFilms() {
        Film film1 = new Film(null, "Film Name 1", "Description 1", LocalDate.parse("2000-01-01"), 120);
        Film film2 = new Film(null, "Film Name 2", "Description 2", LocalDate.parse("2001-01-01"), 130);
        filmController.createFilm(film1);
        filmController.createFilm(film2);

        Collection<Film> films = filmController.findAll();
        assertEquals(2, films.size());
    }
}
