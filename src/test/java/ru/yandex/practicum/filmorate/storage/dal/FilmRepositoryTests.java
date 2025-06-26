package ru.yandex.practicum.filmorate.storage.dal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmRepository.class, GenreRepository.class, MPARepository.class})
class FilmRepositoryTests {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MPARepository mpaRepository;


    @Test
    void testAddAndGetFilm() {
        Film film = new Film();
        film.setName("My Film");
        film.setDescription("Film description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100);

        MPA mpa = mpaRepository.getNameById(1);
        film.setMpa(mpa);


        Film savedFilm = filmRepository.addFilm(film);
        assertTrue(savedFilm.getId() > 0);

        Film filmFromDb = filmRepository.getFilm(savedFilm.getId());
        assertNotNull(filmFromDb);
        assertEquals("My Film", filmFromDb.getName());
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Original Name");
        film.setDescription("Original description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpa(mpaRepository.getNameById(1));
        film.setGenres(Set.of(genreRepository.getGenre(1)));

        Film savedFilm = filmRepository.addFilm(film);
        assertNotNull(savedFilm);

        savedFilm.setName("Updated Name");
        savedFilm.setDescription("Updated description");
        savedFilm.setDuration(150);

        filmRepository.updateFilm(savedFilm);

        Film updatedFilm = filmRepository.getFilm(savedFilm.getId());
        assertEquals("Updated Name", updatedFilm.getName());
        assertEquals("Updated description", updatedFilm.getDescription());
        assertEquals(150, updatedFilm.getDuration());
    }

    @Test
    void testGetAllFilms() {
        Film film1 = new Film();
        film1.setName("Film One");
        film1.setDescription("Description One");
        film1.setReleaseDate(LocalDate.of(2019, 3, 3));
        film1.setDuration(110);
        film1.setMpa(mpaRepository.getNameById(1));
        film1.setGenres(Set.of(genreRepository.getGenre(1)));

        Film film2 = new Film();
        film2.setName("Film Two");
        film2.setDescription("Description Two");
        film2.setReleaseDate(LocalDate.of(2018, 7, 7));
        film2.setDuration(130);
        film2.setMpa(mpaRepository.getNameById(2));
        film2.setGenres(Set.of(genreRepository.getGenre(2)));

        filmRepository.addFilm(film1);
        filmRepository.addFilm(film2);

        var films = filmRepository.getAllFilms();
        assertTrue(films.size() >= 2, "В базе должно быть как минимум 2 фильма");
    }
}
