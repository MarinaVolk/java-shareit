package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        log.info("Запрос на добавление нового пользователя {} ", userDto.getEmail());
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Запрос на обновление данных пользователя с id={} ", userId);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("Запрос на получения списка всех пользователей");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя с id={} ", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя с id={} ", userId);
        userService.deleteUserById(userId);
    }
}
