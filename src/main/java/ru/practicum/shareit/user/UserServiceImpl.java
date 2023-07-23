package ru.practicum.shareit.user;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.EmailValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserEmailAlreadyUsedException;

import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File Name: UserServiceImpl.java
 * Author: Marina Volkova
 * Date: 2023-06-22,   10:48 PM (UTC+3)
 * Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.fromDto(userDto);
        log.info("UserService: Создание пользователя с id={} ", user.getId());
        //user = userRepository.addUser(user);
        //emailCheckOnCreation(user);
        userValidator.isValid(user);
        try {
            return UserMapper.toDto(userRepository.save(user));
        }
        catch (ConstraintViolationException e) {
            throw new EmailValidationException("Email не должен быть пустым.");
        }
        /*catch (Exception e1) {
            throw new UserEmailAlreadyUsedException("Пользователь с таким Email уже создан.");
        }*/
        //user = userRepository.save(user);
        //return UserMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User userUpdated = UserMapper.fromDto(userDto);
        //User oldUser = userRepository.getUserById(userId);
        User oldUser = userRepository.getReferenceById(userId);
        if (!userExistsById(userId)) {
            log.error("Пользователя с id={} в базе нет.", userId);
            throw new NotFoundException("Пользователя с id=" + userId + " в базе нет.");
        }

        log.info("UserService: Обновление пользователя с id={} ", userId);
        User user = userUpdate(userUpdated, oldUser);
        user.setId(userId);
        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        if (!userExistsById(userId)) {
            log.error("Пользователя с id={} в базе нет.", userId);
            throw new NotFoundException("Пользователя с id=" + userId + " в базе нет.");
        }
        User user = userRepository.getReferenceById(userId);
        return UserMapper.toDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Boolean userExistsById(Long userId) {
        return userRepository.existsById(userId);
    }


    private User userUpdate(User userUpdate, User oldUser) {

        User user = new User();

        if (userUpdate.getEmail() != null) {
            user.setEmail(userUpdate.getEmail());
            String email = userUpdate.getEmail();
            String[] lines = email.split("@");
            String newName = lines[0];
            user.setName(newName);
        } else {
            user.setEmail(oldUser.getEmail());
        }

        if (userUpdate.getName() != null) {
            user.setName(userUpdate.getName());
        } else {
            user.setName(oldUser.getName());
        }
        return user;
    }

    private void emailCheckOnCreation(User user) {
        log.info("Проверка Email={} при добавлении пользователя.", user.getEmail());
        userValidator.isValid(user);
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

    private List<String> getAllEmails() {
        return userRepository.findAll().stream().map(User::getEmail).collect(Collectors.toList());
    }

    private Long getUserIdByEmail(String email) {
        for (User user : userRepository.findAll()) {
            if (user.getEmail().equals(email)) {
                return user.getId();
            }
        }
        return -1L;
    }

}
