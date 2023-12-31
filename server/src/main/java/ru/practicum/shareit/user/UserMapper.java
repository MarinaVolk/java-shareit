package ru.practicum.shareit.user;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * File Name: UserMapper.java
 * Author: Marina Volkova
 * Date: 2023-06-22,   7:41 PM (UTC+3)
 * Description:
 */
@Component
public class UserMapper {
    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setId(user.getId());
        return userDto;
    }

    public static User fromDto(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        Optional.ofNullable(userDto.getId()).ifPresent(user::setId);
        return user;
    }
}
