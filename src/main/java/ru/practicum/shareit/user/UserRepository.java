package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User updateUser(User user);

    User getUserById(Long userId);

    void deleteUserById(Long userId);

    List<User> getAll();
}
