package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        try {
            if (film.getName() == null || film.getName().trim().isEmpty()) {
                throw new ValidationException("Название не может быть пустым");
            }

            LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);
            if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(earliestReleaseDate)) {
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }

            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Создан новый фильм: {}", film);
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка при создании фильма: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        try {
            if (film.getId() == 0) {
                throw new ValidationException("Id должен быть указан");
            }
            Film existingFilm = films.get(film.getId());
            if (existingFilm == null) {
                throw new NotFoundException("Фильм не найден");
            }

            if (film.getName() != null && !film.getName().isEmpty()) {
                existingFilm.setName(film.getName());
            }
            if (film.getDescription() != null && film.getDescription().length() <= 200) {
                existingFilm.setDescription(film.getDescription());
            }

            LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);
            if (film.getReleaseDate() != null && !film.getReleaseDate().isBefore(earliestReleaseDate)) {
                existingFilm.setReleaseDate(film.getReleaseDate());
            }
            if (film.getDuration() > 0) {
                existingFilm.setDuration(film.getDuration());
            }
            log.info("Обновлен фильм: {}", existingFilm);
            return existingFilm;
        } catch (ValidationException | NotFoundException e) {
            log.error("Ошибка при обновлении фильма: {}", e.getMessage());
            throw e;
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
