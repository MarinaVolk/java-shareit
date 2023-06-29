package ru.practicum.shareit.user;/* # parse("File Header.java")*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserEmailAlreadyUsedException;
import ru.practicum.shareit.exception.EmailValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File Name: InMemoryUserRepository.java
 * Author: Marina Volkova
 * Date: 2023-06-27,   9:37 PM (UTC+3)
 * Description:
 */
@Repository
@Qualifier("InMemoryUserRepository")
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private Long lastId = 0L;

    @Override
    public User addUser(User user) {
        emailCheckOnCreation(user);

        user.setId(++lastId);
        users.put(lastId, user);

        log.info("Пользователь {} добавлен", user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователя с id={} в базе нет.", user.getId());
            throw new NotFoundException("Пользователя с id=" + user.getId() + " в базе нет.");
        }
        emailCheckOnUpdate(user);
        users.put(user.getId(), user);
        log.info("Данные о пользователе {} обновлены.", user.getEmail());
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            log.error("Пользователя с id={} в базе нет.", userId);
            throw new NotFoundException("Такого пользователя не существует.");
        }
        return users.get(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!users.containsKey(userId)) {
            log.error("Пользователя с id={} в базе нет.", userId);
            throw new NotFoundException("Такого пользователя в базе нет.");
        }
        users.remove(userId);
        log.info("Пользователь с id={} удалён.", userId);
    }

    private List<String> getAllEmails() {
        return users.values().stream().map(User::getEmail).collect(Collectors.toList());
    }

    private void emailCheckOnCreation(User user) {
        log.info("Проверка Email={} при добавлении пользователя.", user.getEmail());
        if (user.getEmail() == null) {
            throw new EmailValidationException("При создании пользователя не указан Email.");
        }
        if (!user.getEmail().contains("@")) {
            throw new EmailValidationException("Электронная почта должна содержать символ \"@\".");
        }
        if (getAllEmails().contains(user.getEmail())) {
            log.error("Email={} занят", user.getEmail());
            throw new UserEmailAlreadyUsedException("Пользователь с таким Email уже создан.");
        }
    }

    private void emailCheckOnUpdate(User user) {
        log.info("Проверка Email={} при изменении пользователя.", user.getEmail());
        if (getAllEmails().contains(user.getEmail()) && !getUserIdByEmail(user.getEmail()).equals(user.getId())) {
            log.error("Пользователь с Email={} уже создан.", user.getEmail());
            throw new UserEmailAlreadyUsedException("Пользователь с таким Email уже создан.");
        }
    }

    private Long getUserIdByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return user.getId();
            }
        }
        return -1L;
    }

}
