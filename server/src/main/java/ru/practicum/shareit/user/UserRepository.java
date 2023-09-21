package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // Если найден email не принадлежащий пользователю, то обновить данные нельзя
    @Query("select case when (count(u) >0) then true else false end " +
            "from User as u " +
            "where lower(u.email) like lower(?1)")
    boolean canNotUpdate(String email);

    List<User> findUsersByEmail(String email);
}
