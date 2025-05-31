package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;


public interface FilmStorage {

    Film getFilm(long id);

    Film addFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(long id);
}
