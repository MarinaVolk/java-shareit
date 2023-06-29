package ru.practicum.shareit.user;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final InMemoryUserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.fromDto(userDto);
        log.info("UserService: Создание пользователя с id={} ", user.getId());
        user = userRepository.addUser(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User userUpdated = UserMapper.fromDto(userDto);
        User oldUser = userRepository.getUserById(userId);

        log.info("UserService: Обновление пользователя с id={} ", userId);
        User user = userUpdate(userUpdated, oldUser);
        user.setId(userId);
        user = userRepository.updateUser(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.getUserById(userId);
        return UserMapper.toDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        List<User> allUsers = userRepository.getAll();
        return allUsers.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteUserById(userId);
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

}
