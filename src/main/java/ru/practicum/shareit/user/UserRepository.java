package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    /*User addUser(User user);

    User updateUser(User user);

    User getUserById(Long userId);

    void deleteUserById(Long userId);

    List<User> getAll();*/
}
