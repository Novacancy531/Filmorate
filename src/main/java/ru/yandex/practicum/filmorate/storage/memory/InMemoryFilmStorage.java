package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films;
    private final HashMap<Long, Set<Long>> likes = new HashMap<>();
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
    public Map<Long, Film> getAllFilms() {
        return new HashMap<>(films);
    }

    @Override
    public List<Film> mostPopular(long count) {
        List<Film> topFilms = new ArrayList<>();
        Set<Long> topFilmsId = likes.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size())) // по убыванию размера Set
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        for (Long id : topFilmsId) {
            topFilms.add(getFilm(id));
        }

        return topFilms;
    }

    @Override
    public void addLike(long userId, long filmId) {
        Set<Long> filmsLike = likes.get(filmId);
        if (filmsLike == null) {
            filmsLike = new HashSet<>();
        }
        filmsLike.add(userId);
        likes.replace(filmId, filmsLike);
    }

    @Override
    public void deleteLike(long userId, long filmId) {
        Set<Long> filmsLike = likes.get(filmId);
        if (filmsLike == null) {
            filmsLike = new HashSet<>();
        }
        filmsLike.remove(userId);
        likes.replace(filmId, filmsLike);
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
