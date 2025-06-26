package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;


public interface GenreStorage {

    Genre getGenre(long id);

    Set<Genre> getAllGenres();

    Set<Genre> findGenresByFilmId(long filmId);

    void saveFilmGenres(Film film);

    void deleteFilmsGenres(long id);

    void validateGenres(Film film);
}
