package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;

import java.util.Collection;


@RestController
@Validated
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FilmDto getFilm(@PathVariable final long id) {
        return filmService.getFilm(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> mostPopular(@RequestParam(defaultValue = "10") final long count) {
        return filmService.mostPopular(count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto createFilm(@Valid @RequestBody final FilmDto film) {
        return filmService.createFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void likeFilm(@PathVariable final long id, @PathVariable final long userId) {
        filmService.addLike(id, userId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public FilmDto updateFilm(@Valid @RequestBody final FilmDto film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFilm(@PathVariable final long id) {
        filmService.deleteFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable final long id, @PathVariable final long userId) {
        filmService.deleteLike(id, userId);
    }
}
