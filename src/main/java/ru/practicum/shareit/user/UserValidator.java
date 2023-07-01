package ru.practicum.shareit.user;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ValidationException;

/**
 * File Name: UserValidator.java
 * Author: Marina Volkova
 * Date: 2023-06-30,   11:23 AM (UTC+3)
 * Description:
 */

@Component
public class UserValidator {
    public void isValid(User user) throws ValidationException {
        validateEmail(user.getEmail());
        validateName(user.getName());

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getName());
        }
    }

    private void validateEmail(String email) throws ValidationException {
        if (!StringUtils.hasText(email)) {
            throw new ValidationException("Электронная почта не может быть пустой.");
        }
        if (!email.contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ \"@\".");
        }
    }

    private void validateName(String login) throws ValidationException {
        if (!StringUtils.hasText(login)) {
            throw new ValidationException("Имя не может быть пустым.");
        }
        if (login.contains(" ")) {
            throw new ValidationException("Имя не может содержать пробелы.");
        }
    }

}
