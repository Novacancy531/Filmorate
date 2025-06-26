package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.interfaces.MPAStorage;

import java.util.Set;


@Slf4j
@Service
@AllArgsConstructor
public class MPAService {
    private final MPAStorage mpaStorage;


    public MPA getNameById(final long id) {
        log.info("Отправляется запрошенный рейтинг.");
        return mpaStorage.getNameById(id);
    }

    public Set<MPA> getAllMPA() {
        log.info("Отправляется список всех рейтингов.");
        return mpaStorage.getAllMPA();
    }
}
