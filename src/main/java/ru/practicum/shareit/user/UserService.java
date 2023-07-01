package ru.practicum.shareit.user;

import ru.practicum.shareit.user.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto getUserById(Long userId);

    Collection<UserDto> getAll();

    void deleteUserById(Long userId);
}
