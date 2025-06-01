package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;


public interface FilmStorage {

    Film getFilm(long id);

    Map<Long, Film> getFilms();

    Film addFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(long id);
}
