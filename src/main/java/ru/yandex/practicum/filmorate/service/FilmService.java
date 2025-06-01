package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        checkFilmExists(film.getId());

        filmStorage.updateFilm(film);
        log.info("Фильм с id: {} обновлен.", film.getId());
        return filmStorage.getFilm(film.getId());
    }

    public void deleteFilm(final long filmId) {
        checkFilmExists(filmId);

        log.info("Удален фильм с id: {}", filmId);
        filmStorage.deleteFilm(filmId);
    }

    public void addLike(final long filmId, final long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        if (filmStorage.getFilm(filmId).getLikes().contains(userId)) {
            log.warn("Пользователь с id: {} пытается повторно поставить лайк", userId);
            throw new ConditionsNotMetException("Ваш лайк уже поставлен.");
        }

        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, filmId);
        filmStorage.getFilm(filmId).getLikes().add(userId);
    }

    public void deleteLike(final long filmId, final long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        if (!filmStorage.getFilm(filmId).getLikes().contains(userId)) {
            log.warn("Пользователь с id: {} пытается убрать отсутствующий лайк", userId);
            throw new ConditionsNotMetException("На данном фильме нет вашего лайка");
        }

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

    public void checkUserExists(final long userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден.");
        }
    }

    public void checkFilmExists(final long filmId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new NotFoundException("Фильм с id: " + filmId + " не найден.");
        }
    }
}
