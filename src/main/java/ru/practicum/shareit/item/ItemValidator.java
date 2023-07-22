package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ValidationException;

/**
 * File Name: ItemValidator.java
 * Author: Marina Volkova
 * Date: 2023-06-30,   11:22 AM (UTC+3)
 * Description:
 */
@Component
public class ItemValidator {
    public void isValid(Item item) throws ValidationException {
        validateName(item.getName());
        validateDescription(item.getDescription());
        validateAvailability(item.getIsAvailable());
    }

    private void validateName(String name) throws ValidationException {
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("Наименование вещи не может быть пустым.");
        }
    }

    private void validateDescription(String description) throws ValidationException {
        if (!StringUtils.hasText(description)) {
            throw new ValidationException("Описание вещи не может быть пустым.");
        }
    }

    private void validateAvailability(Boolean availability) throws ValidationException {
        if (availability == null) {
            throw new ValidationException("Не указан статус доступности вещи.");
        }
    }

}
