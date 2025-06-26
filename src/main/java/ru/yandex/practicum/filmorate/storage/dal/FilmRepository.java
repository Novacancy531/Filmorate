package ru.yandex.practicum.filmorate.storage.dal;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdGenerationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;


@Repository
@AllArgsConstructor
public class FilmRepository implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRepository genreRepository;
    private final MPARepository mpaRepository;
    private static final String ADD_FILM = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_FILM = "SELECT * FROM films WHERE film_id = ?";
    private static final String RETURN_ALL_FILMS = "SELECT * FROM films";
    private static final String DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    private static final String ADD_LIKE = "MERGE INTO likes (user_id, film_id) KEY (user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_LIKE = "DELETE from likes WHERE user_id = ? AND film_id = ?";
    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String MOST_POPULAR = "SELECT film_id FROM likes GROUP BY film_id ORDER BY COUNT(film_id) desc LIMIT ?";


    @Override
    public Film addFilm(final Film film) {
        mpaRepository.validateMpa(film);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_FILM, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IdGenerationException("Фильму не присвоен id.");
        }
        film.setId(key.longValue());

        genreRepository.saveFilmGenres(film);
        return film;
    }

    @Override
    public Film getFilm(final long id) {
        return jdbcTemplate.queryForObject(FIND_FILM, this::mapRowToFilm, id);
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        List<Film> films = jdbcTemplate.query(RETURN_ALL_FILMS, this::mapRowToFilm);
        Map<Long, Film> filmMap = new HashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }
        return filmMap;
    }

    @Override
    public void updateFilm(final Film film) {
        jdbcTemplate.update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        genreRepository.deleteFilmsGenres(film.getId());
        genreRepository.saveFilmGenres(film);
    }

    @Override
    public void deleteFilm(final long id) {
        genreRepository.deleteFilmsGenres(id);
        jdbcTemplate.update(DELETE_FILM, id);
    }

    @Override
    public void addLike(final long userId, final long filmId) {
        jdbcTemplate.update(ADD_LIKE, userId, filmId);
    }

    @Override
    public void deleteLike(final long userId, final long filmId) {
        jdbcTemplate.update(DELETE_LIKE, userId, filmId);
    }

    @Override
    public List<Film> mostPopular(final long count) {
        List<Long> popularFilmsId = jdbcTemplate.queryForList(MOST_POPULAR, Long.class, count);
        List<Film> popularFilms = new LinkedList<>();
        for (Long filmId : popularFilmsId) {
            popularFilms.add(getFilm(filmId));
        }

        return popularFilms;
    }

    private Film mapRowToFilm(final ResultSet rs, final int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        film.setMpa(mpaRepository.getNameById(rs.getInt("mpa_id")));

        Set<Genre> genres = genreRepository.findGenresByFilmId(film.getId());
        film.setGenres(genres);

        return film;
    }
}
