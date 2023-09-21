package ru.practicum.shareit.user;/* # parse("File Header.java")*/

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * File Name: UserValidatorTest.java
 * Author: Marina Volkova
 * Date: 2023-09-17,   8:18 PM (UTC+3)
 * Description:
 */
@SpringBootTest
public class UserValidatorTest {
    @Autowired
    private UserValidator validator;

    @Test
    public void isValid_throwException_EmailIsAbsent() {
        User user = new User();
        user.setName("user1");

        assertThrows(ValidationException.class, () -> validator.isValid(user),
                "Ожидалось исключение, но не произошло."
        );
    }

    @Test
    public void isValid_throwException_IncorrectEmailAddress() {
        User user = new User();
        user.setName("user1");
        user.setEmail("user1");

        assertThrows(ValidationException.class, () -> validator.isValid(user),
                "Ожидалось исключение, но не произошло."
        );
    }

    @Test
    public void isValid_throwException_NameIsAbsent() {
        User user = new User();
        user.setEmail("ru.practicum.shareit.user@email.com");

        assertThrows(ValidationException.class, () -> validator.isValid(user),
                "Ожидалось исключение, но не произошло."
        );
    }

    @Test
    public void isValid_throwException_NameIsEmpty() {
        User user = new User();
        user.setEmail("ru.practicum.shareit.user@email.com");
        user.setName("");

        assertThrows(ValidationException.class, () -> validator.isValid(user),
                "Ожидалось исключение, но не произошло."
        );
    }
}