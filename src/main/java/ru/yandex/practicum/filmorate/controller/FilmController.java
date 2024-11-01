package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    HashMap<Long, Film> films = new HashMap<>();
    private String warn = "Переданы некорректные данные";

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn(warn);
            throw new ConditionsNotMetException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > 200) {
                log.warn(warn);
                throw new ConditionsNotMetException("Описание фильма не может быть более 200 символов");
            }
        }
        LocalDate dateOfTheFirstFilm = LocalDate.of(1895, 12, 28);

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(dateOfTheFirstFilm)) {
            log.warn(warn);
            throw new ConditionsNotMetException("Дата релиза фильма не может быть раньше даты релиза самого первого в мире фильма");
        }

        if (film.getDuration() < 0) {
            log.warn(warn);
            throw new ConditionsNotMetException("продолжительность фильма не может быть отрицательной");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("Добавился новый фильм - {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film.getId() == null) {

            log.warn(warn);
            throw new ConditionsNotMetException("Для обновления данных фильма необходимо указать Id");
        }
        if (!films.containsKey(film.getId())) {
            log.warn("Информация не найдена");
            throw new NotFoundException("Фильм с указанным Id не найден - невозможно обновить данные");
        }

        Film oldFilm = films.get(film.getId());
        if (film.getName() != null && !film.getName().isBlank()) {
            oldFilm.setName(film.getName());
        }
        if (film.getDescription() != null && film.getDescription().length() <= 200) {
            oldFilm.setDescription(film.getDescription());
        }
        LocalDate dateOfTheFirstFilm = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() != null && !film.getReleaseDate().isBefore(dateOfTheFirstFilm)) {
            oldFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() > 0) {
            oldFilm.setDuration(film.getDuration());
        }

        log.debug("Обновление фильма произошло успешно. Id - {}", oldFilm.getId());
        return oldFilm;

    }

    @GetMapping
    public List<Film> getAll() {
        List<Film> allFilms = films.values().stream().toList();
        log.info("Отправлен весь список фильмов");
        return allFilms;
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
