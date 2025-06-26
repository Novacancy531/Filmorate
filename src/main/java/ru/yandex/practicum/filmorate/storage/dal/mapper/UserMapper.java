package ru.yandex.practicum.filmorate.storage.dal.mapper;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dto.UserDto;


public class UserMapper {

    private UserMapper() {
    }


    public static UserDto toDto(final User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
    }

    public static User fromDto(final UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setLogin(dto.getLogin());
        user.setName(dto.getName());
        user.setBirthday(dto.getBirthday());
        return user;
    }
}
