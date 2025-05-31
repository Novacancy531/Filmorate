package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.HashMap;

@Component
@Data
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films;
    private long currentLastId;


    @Override
    public Film getFilm(final long id) {
        return films.get(id);
    }

    @Override
    public Film addFilm(final Film film) {
        film.setId(getNewId());
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public void updateFilm(final Film film) {
        films.replace(film.getId(), film);
    }

    @Override
    public void deleteFilm(final long id) {
        films.remove(id);
    }

    private Long getNewId() {
        return ++currentLastId;
    }
}
