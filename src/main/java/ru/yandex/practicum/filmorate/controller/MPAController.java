package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.Set;


@RestController
@RequestMapping("/mpa")
@Validated
@RequiredArgsConstructor
public class MPAController {
    private final MPAService mpaService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Set<MPA> getAllMPA() {
        return mpaService.getAllMPA();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MPA getGenre(@PathVariable final long id) {
        return mpaService.getNameById(id);
    }
}
