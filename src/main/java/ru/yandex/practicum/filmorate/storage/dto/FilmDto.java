package ru.yandex.practicum.filmorate.storage.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.validator.annotation.AfterDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmDto {

    private Long id;

    @NotBlank(message = "Название не может быть пустым.")
    private String name;

    @NotBlank(message = "Описание не может быть пустым.")
    @Size(max = 200, message = "Описание не может превышать 200 символов.")
    private String description;

    @NotNull(message = "Дата релиза обязательна.")
    @AfterDate(value = "1895-12-27", message = "Дата должна быть не ранее 28.12.1895.")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность обязательна.")
    @Min(value = 1, message = "Продолжительность должна быть больше 0.")
    private Integer duration;

    private MPA mpa;

    private Set<Genre> genres = new HashSet<>();
}
