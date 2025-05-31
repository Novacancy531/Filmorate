package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;


    public Film createFilm(final Film film) {
        log.info("Создан фильм: {}", film.getName());
        return filmStorage.addFilm(film);
    }

    public Collection<Film> getAllFilms() {
        log.info("Отправлен список всех фильмов.");
        return filmStorage.getFilms().values();
    }

    public Film updateFilm(final Film film) {
        if (film.getId() == null) {
            throw new ConditionsNotMetException("Не указан id фильма.");
        }
        ifContainsFilm(film.getId());

        log.info("Фильм с id: {} обновлен.", film.getId());
        filmStorage.updateFilm(film);
        return filmStorage.getFilm(film.getId());
    }

    public void deleteFilm(final long filmId) {
        ifContainsFilm(filmId);

        log.info("Удален фильм с id: {}", filmId);
        filmStorage.deleteFilm(filmId);
    }

    public void addLike(final long filmId, final long userId) {
        ifContainsFilm(filmId);
        ifContainsUser(userId);

        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, filmId);
        filmStorage.getFilm(filmId).getLikes().add(userId);
    }

    public void deleteLike(final long filmId, final long userId) {
        ifContainsFilm(filmId);
        ifContainsUser(userId);

        log.info("У фильма с id: {} удален лайк пользователя с id: {}.", filmId, userId);
        filmStorage.getFilm(filmId).getLikes().remove(userId);
    }

    public Collection<Film> mostPopular(final Long count) {
        long limit = (count == null || count <= 0) ? 10 : count;

        log.info("Отправлен список популярных фильмов");
        return filmStorage.getFilms().values().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(limit)
                .toList();
    }

    public void ifContainsUser(final long userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            log.info("Пользователь с id: {} не найден.", userId);
            throw new ConditionsNotMetException("Пользователь с id: " + userId + " не найден.");
        }
    }

    public void ifContainsFilm(final long filmId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            log.info("Фильм с id: {} не найден.", filmId);
            throw new ConditionsNotMetException("Фильм с id: " + filmId + " не найден.");
        }
    }
}
