package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        User user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException("Попытка добавить лайк несуществующим пользователем");
        }

        Set<Long> likes = film.getLikes();
        if (likes == null) {
            likes = new HashSet<>();
            film.setLikes(likes);
        }

        likes.add(userId);
        filmStorage.update(film);
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        User user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException("Попытка удалить лайк несуществующим пользователем");
        }

        film.getLikes().remove(userId);
        filmStorage.update(film);
    }


    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}