package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmStorage {

    Film addFilm(Film film);

    Film getFilm(long id);

    List<Film> getAllFilms();

    List<Film> mostPopular(long count);

    void addLike(long userId, long filmId);

    void deleteLike(long userId, long filmId);

    void updateFilm(Film film);

    void deleteFilm(long id);
}
