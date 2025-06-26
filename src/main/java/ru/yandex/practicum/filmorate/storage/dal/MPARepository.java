package ru.yandex.practicum.filmorate.storage.dal;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.interfaces.MPAStorage;

import java.util.*;


@Repository
@AllArgsConstructor
public class MPARepository implements MPAStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String RETURN_MPA = "SELECT * FROM mpa WHERE mpa_id = ?";
    private static final String RETURN_ALL_MPA = "SELECT * FROM mpa ORDER BY mpa_id";
    private static final String RETURN_ALL_MPA_ID = "SELECT mpa_id FROM mpa";
    private static final String FIND_MPA_BY_ID = "SELECT films.mpa_id, mpa.name FROM films " +
            "join mpa on films.mpa_id = mpa.mpa_id WHERE film_id = ? ORDER BY films.mpa_id";


    @Override
    public MPA getNameById(final long id) {
        List<MPA> mpaList = jdbcTemplate.query(RETURN_MPA, (rs, rowNum) -> {
            MPA mpa = new MPA();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        }, id);

        if (mpaList.isEmpty()) {
            throw new NotFoundException("Рейтинг не найден.");
        }

        return mpaList.getFirst();
    }

    @Override
    public Set<MPA> getAllMPA() {
        List<MPA> mpaList = jdbcTemplate.query(RETURN_ALL_MPA, (rs, rowNum) -> {
            MPA mpa = new MPA();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        });
        return new LinkedHashSet<>(mpaList);
    }

    public void validateMpa(final Film film) {
        List<Integer> genreIdsInBase = jdbcTemplate.queryForList(RETURN_ALL_MPA_ID, Integer.class);

        if (!genreIdsInBase.contains(film.getMpa().getId())) {
            throw new NotFoundException("Рейтинг отсутствующий в таблице.");
        }
    }
}
