package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dal.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.MPAStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;


@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MPAStorage mpaStorage;


    public FilmDto createFilm(final FilmDto filmDto) {
        log.info("Добавляется фильм: {}", filmDto.getName());
        Film film = FilmMapper.fromDto(filmDto);
        mpaStorage.validateMpa(film);
        filmStorage.addFilm(film);
        genreStorage.saveFilmGenres(film);

        return FilmMapper.toDto(film);
    }

    public FilmDto getFilm(final long id) {
        Film film = filmStorage.getFilm(id);
        film.setGenres(genreStorage.findGenresByFilmId(id));

        return FilmMapper.toDto(film);
    }

    public Collection<FilmDto> getAllFilms() {
        log.info("Отправляется список всех фильмов.");
        return filmStorage.getAllFilms().stream()
                .peek(film -> film.setGenres(genreStorage.findGenresByFilmId(film.getId())))
                .map(FilmMapper::toDto)
                .toList();
    }

    public FilmDto updateFilm(final FilmDto filmDto) {
        if (filmDto.getId() == null) {
            throw new ConditionsNotMetException("Не указан id фильма.");
        }
        Film film = FilmMapper.fromDto(filmDto);

        checkFilmExists(film.getId());
        genreStorage.deleteFilmsGenres(film.getId());

        filmStorage.updateFilm(film);
        genreStorage.saveFilmGenres(film);

        log.info("Фильм с id: {} обновлен.", film.getId());
        return FilmMapper.toDto(filmStorage.getFilm(film.getId()));
    }

    public void deleteFilm(final long filmId) {
        checkFilmExists(filmId);

        genreStorage.deleteFilmsGenres(filmId);
        filmStorage.deleteFilm(filmId);
        log.info("Удален фильм с id: {}", filmId);
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
        if (filmStorage.getAllFilms().stream()
                .noneMatch(film -> film.getId() == filmId)) {
            throw new NotFoundException("Фильм с id: " + filmId + " не найден.");
        }
    }
}
