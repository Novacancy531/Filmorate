package ru.yandex.practicum.filmorate.storage.dal;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.*;


@Repository
@AllArgsConstructor
public class GenreRepository implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String GENRE_ID = "genre_id";
    private static final String RETURN_GENRE = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String RETURN_ALL_GENRES = "SELECT * FROM genre ORDER BY genre_id";
    private static final String RETURN_ALL_GENRES_ID = "SELECT genre_id FROM genre";
    private static final String SAVE_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETING_FILM_GENRES = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String FIND_GENRES_BY_FILM_ID = "SELECT film_genre.genre_id, name FROM film_genre " +
            "join genre on film_genre.genre_id = genre.genre_id WHERE film_id = ? ORDER BY film_genre.genre_id";


    @Override
    public Genre getGenre(final long id) {
        List<Genre> genreList = jdbcTemplate.query(RETURN_GENRE, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt(GENRE_ID));
            genre.setName(rs.getString("name"));
            return genre;
        }, id);

        if (genreList.isEmpty()) {
            throw new NotFoundException("Жанр не найден.");
        }

        return genreList.getFirst();
    }

    @Override
    public Set<Genre> getAllGenres() {
        List<Genre> genres = jdbcTemplate.query(RETURN_ALL_GENRES, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt(GENRE_ID));
            genre.setName(rs.getString("name"));
            return genre;
        });
        return new LinkedHashSet<>(genres);
    }

    @Override
    public Set<Genre> findGenresByFilmId(final long filmId) {
        Set<Genre> genres = new HashSet<>();
        jdbcTemplate.query(FIND_GENRES_BY_FILM_ID, genreRs -> {
            Genre genre = new Genre();
            genre.setId(genreRs.getInt(GENRE_ID));
            genre.setName(genreRs.getString("name"));
            genres.add(genre);
        }, filmId);

        return genres;
    }

    @Override
    public void saveFilmGenres(final Film film) {
        validateGenres(film);

        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(SAVE_GENRE, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void deleteFilmsGenres(final long id) {
        jdbcTemplate.update(DELETING_FILM_GENRES, id);
    }

    @Override
    public void validateGenres(final Film film) {
        List<Integer> genreIdsInBase = jdbcTemplate.queryForList(RETURN_ALL_GENRES_ID, Integer.class);

        for (Genre genre : film.getGenres()) {
            if (!genreIdsInBase.contains(genre.getId())) {
                throw new NotFoundException("Жанр отсутствует.");
            }
        }
    }
}
