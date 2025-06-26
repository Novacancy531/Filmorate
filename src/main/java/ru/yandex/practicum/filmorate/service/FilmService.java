package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.dal.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;


@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("filmRepository") final FilmStorage filmStorage,
                       @Qualifier("userRepository") final UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }


    public FilmDto createFilm(final FilmDto film) {
        log.info("Добавляется фильм: {}", film.getName());
        return FilmMapper.toDto(filmStorage.addFilm(FilmMapper.fromDto(film)));
    }

    public FilmDto getFilm(final long id) {
        return FilmMapper.toDto(filmStorage.getFilm(id));
    }

    public Collection<FilmDto> getAllFilms() {
        log.info("Отправляется список всех фильмов.");
        return filmStorage.getAllFilms().values().stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    public FilmDto updateFilm(final FilmDto film) {
        if (film.getId() == null) {
            throw new ConditionsNotMetException("Не указан id фильма.");
        }
        checkFilmExists(film.getId());

        filmStorage.updateFilm(FilmMapper.fromDto(film));
        log.info("Фильм с id: {} обновлен.", film.getId());
        return FilmMapper.toDto(filmStorage.getFilm(film.getId()));
    }

    public void deleteFilm(final long filmId) {
        checkFilmExists(filmId);

        log.info("Удален фильм с id: {}", filmId);
        filmStorage.deleteFilm(filmId);
    }

    public void addLike(final long filmId, final long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        filmStorage.addLike(userId, filmId);
        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, filmId);
    }

    public void deleteLike(final long filmId, final long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        filmStorage.deleteLike(userId, filmId);
        log.info("У фильма с id: {} удален лайк пользователя с id: {}.", filmId, userId);
    }

    public Collection<FilmDto> mostPopular(final long count) {
        log.info("Отправлен список популярных фильмов");
        return filmStorage.mostPopular(count).stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    public void checkUserExists(final long userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден.");
        }
    }

    public void checkFilmExists(final long filmId) {
        if (!filmStorage.getAllFilms().containsKey(filmId)) {
            throw new NotFoundException("Фильм с id: " + filmId + " не найден.");
        }
    }
}
