package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;

@SpringBootTest
class FilmTests {
    private static Validator validate;
    private static Set<ConstraintViolation<Film>> violations;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = buildDefaultValidatorFactory()) {
            validate = factory.getValidator();
        }
    }

    private Film createFilm() {
        Film film = new Film();
        MPA mpa = new MPA();
        mpa.setId(2);

        film.setName("Фильм");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(100);
        film.setMpa(mpa);

        return film;
    }

    @Test
    void validFilm() {
        var film = createFilm();

        violations = validate.validate(film);
        Assertions.assertTrue(violations.isEmpty(), "Валидация не пройдена");
    }

    @Test
    void validName() {
        var film = createFilm();

        film.setName(" ");
        violations = validate.validate(film);
        Assertions.assertFalse(violations.isEmpty(), "Ожидалось что поле не пройдет валидацию.");
        Assertions.assertTrue(violations.stream().anyMatch(violation -> violation
                .getPropertyPath().toString().equals("name")), "Ожидалось поле name.");
    }

    @Test
    void validDescription() {
        var film = createFilm();

        film.setDescription(" ");
        violations = validate.validate(film);
        Assertions.assertFalse(violations.isEmpty(), "Ожидалось что поле не пройдет валидацию.");
        Assertions.assertTrue(violations.stream().anyMatch(violation -> violation
                .getPropertyPath().toString().equals("description")), "Ожидалось поле description.");

        film.setDescription("a".repeat(201));
        violations = validate.validate(film);
        Assertions.assertFalse(violations.isEmpty(), "Ожидалось что поле не пройдет валидацию.");
        Assertions.assertTrue(violations.stream().anyMatch(violation -> violation
                .getPropertyPath().toString().equals("description")), "Ожидалось поле description.");
    }

    @Test
    void validReleaseDate() {
        var film = createFilm();

        film.setReleaseDate(null);
        violations = validate.validate(film);
        Assertions.assertFalse(violations.isEmpty(), "Ожидалось что поле не пройдет валидацию.");
        Assertions.assertTrue(violations.stream().anyMatch(violation -> violation
                .getPropertyPath().toString().equals("releaseDate")), "Ожидалось поле releaseDate.");

        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        violations = validate.validate(film);
        Assertions.assertFalse(violations.isEmpty(), "Ожидалось что поле не пройдет валидацию.");
        Assertions.assertTrue(violations.stream().anyMatch(violation -> violation
                .getPropertyPath().toString().equals("releaseDate")), "Ожидалось поле releaseDate.");
    }

    @Test
    void validDuration() {
        var film = createFilm();

        film.setDuration(0);
        violations = validate.validate(film);
        Assertions.assertFalse(violations.isEmpty(), "Ожидалось что поле не пройдет валидацию.");
        Assertions.assertTrue(violations.stream().anyMatch(violation -> violation
                .getPropertyPath().toString().equals("duration")), "Ожидалось поле duration.");

        film.setDuration(2);
        violations = validate.validate(film);
        Assertions.assertTrue(violations.isEmpty(), "Ожидалось что поле пройдет валидацию.");
    }
}
