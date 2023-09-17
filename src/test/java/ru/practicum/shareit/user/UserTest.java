package ru.practicum.shareit.user;/* # parse("File Header.java")*/

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * File Name: UserTest.java
 * Author: Marina Volkova
 * Date: 2023-09-17,   8:18 PM (UTC+3)
 * Description:
 */
@SpringBootTest
public class UserTest {
    @Autowired
    UserValidator validator;

    @Test
    public void createInvalidUsersTest() {
        Collection<User> users = new ArrayList<>();

        //Без адреса email
        User user = new User();
        user.setName("user1");
        users.add(user);

        //Некорректный адрес email
        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2");
        users.add(user2);

        //Без имени
        User user3 = new User();
        user3.setEmail("user3@email.com");
        users.add(user3);

        //Пустое имя
        User user4 = new User();
        user4.setEmail("user4@email.com");
        user4.setName("");
        users.add(user4);

        users.forEach(x -> assertThrows(ValidationException.class, () -> validator.isValid(x)));
    }
}