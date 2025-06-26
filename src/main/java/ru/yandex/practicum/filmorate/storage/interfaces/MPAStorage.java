package ru.yandex.practicum.filmorate.storage.interfaces;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Set;

public interface MPAStorage {
    MPA getNameById(long id);

    Set<MPA> getAllMPA();

    void validateMpa(Film film);
}
