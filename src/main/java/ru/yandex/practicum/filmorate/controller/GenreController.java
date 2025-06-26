package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Set;


@RestController
@RequestMapping("/genres")
@Validated
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Set<Genre> getAllGenres() {
        return genreService.getAllGenre();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Genre getGenre(@PathVariable final long id) {
        return genreService.getGenre(id);
    }
}
