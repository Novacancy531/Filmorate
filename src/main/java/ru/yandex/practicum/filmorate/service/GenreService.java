package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.Set;


@Slf4j
@Service
@AllArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;


    public Genre getGenre(Long id) {
        log.info("Отправляется запрошенный жанр.");
        return genreStorage.getGenre(id);
    }

    public Set<Genre> getAllGenre() {
        log.info("Отправляется список всех жанров");
        return genreStorage.getAllGenres();
    }
}
