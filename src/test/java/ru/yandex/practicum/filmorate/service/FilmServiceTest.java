package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {

    private FilmService filmService;
    private InMemoryFilmStorage filmStorage;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);  // передаем оба хранилища
    }

    @Test
    void testAddLikeSuccess() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.parse("2000-01-01"), 120, new HashSet<>());
        Film createdFilm = filmStorage.create(film);

        // Create a valid user
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("validLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.parse("1990-01-01"));
        userStorage.create(user);

        filmService.addLike(createdFilm.getId(), user.getId());
        assertTrue(createdFilm.getLikes().contains(user.getId()));
    }

    @Test
    void testAddLikeFilmNotFound() {
        assertThrows(NotFoundException.class, () -> filmService.addLike(999L, 1L));
    }

    @Test
    void rtestRemoveLikeSuccess() {
        Film film = new Film(null, "Film Name", "Description", LocalDate.parse("2000-01-01"), 120, new HashSet<>());
        Film createdFilm = filmStorage.create(film);

        // Create a valid user
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("validLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.parse("1990-01-01"));
        userStorage.create(user);

        filmService.addLike(createdFilm.getId(), user.getId());
        filmService.removeLike(createdFilm.getId(), user.getId());
        assertFalse(createdFilm.getLikes().contains(user.getId()));
    }

    @Test
    void testRemoveLikeFilmNotFound() {
        assertThrows(NotFoundException.class, () -> filmService.removeLike(999L, 1L));
    }

    @Test
    void testGetMostPopularFilms() {
        Film film1 = new Film(null, "Film Name 1", "Description 1", LocalDate.parse("2000-01-01"), 120, new HashSet<>());
        Film film2 = new Film(null, "Film Name 2", "Description 2", LocalDate.parse("2000-01-01"), 130, new HashSet<>());
        Film createdFilm1 = filmStorage.create(film1);
        Film createdFilm2 = filmStorage.create(film2);

        // Create a valid user
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("validLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.parse("1990-01-01"));
        userStorage.create(user);

        filmService.addLike(createdFilm2.getId(), user.getId());
        List<Film> popularFilms = filmService.getMostPopularFilms(1);
        assertEquals(1, popularFilms.size());
        assertEquals(createdFilm2.getId(), popularFilms.get(0).getId());
    }

    @Test
    void testCreateUserInvalidLogin() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("invalid login"); // Contains a space
        user.setName("User Name");
        user.setBirthday(LocalDate.parse("1990-01-01"));

        assertThrows(ConstraintViolationException.class, () -> userStorage.create(user));
    }


}
