package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryFilmStorageTest {

    private InMemoryFilmStorage filmStorage;

    @BeforeEach
    protected void setUp() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    protected void createFilm_success() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.of(2000, 1, 1), 120, null);
        Film createdFilm = filmStorage.create(film);
        assertNotNull(createdFilm.getId());
        assertEquals("Film Name", createdFilm.getName());
    }

    @Test
    protected void createFilm_invalidReleaseDate() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.of(1800, 1, 1), 120, null);
        assertThrows(ValidationException.class, () -> filmStorage.create(film));
    }

    @Test
    protected void updateFilm_success() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.of(2000, 1, 1), 120, null);
        Film createdFilm = filmStorage.create(film);
        createdFilm.setName("Updated Name");
        Film updatedFilm = filmStorage.update(createdFilm);
        assertEquals("Updated Name", updatedFilm.getName());
    }

    @Test
    protected void updateFilm_notFound() {
        Film film = new Film(999L, "Film Name", "Description", LocalDate.of(2000, 1, 1), 120, null);
        assertThrows(NotFoundException.class, () -> filmStorage.update(film));
    }

    @Test
    protected void findAllFilms() {
        Film film1 = new Film(null, "Film Name 1", "Description 1", LocalDate.of(2000, 1, 1), 120, null);
        Film film2 = new Film(null, "Film Name 2", "Description 2", LocalDate.of(2001, 1, 1), 130, null);
        filmStorage.create(film1);
        filmStorage.create(film2);

        Collection<Film> films = filmStorage.findAll();
        assertEquals(2, films.size());
    }

    @Test
    protected void findFilmById_success() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.of(2000, 1, 1), 120, null);
        Film createdFilm = filmStorage.create(film);
        Film foundFilm = filmStorage.findById(createdFilm.getId());
        assertNotNull(foundFilm);
    }

    @Test
    protected void findFilmById_notFound() {
        assertNull(filmStorage.findById(999L));
    }
}
