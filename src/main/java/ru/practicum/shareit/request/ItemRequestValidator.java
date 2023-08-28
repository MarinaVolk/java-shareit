package ru.practicum.shareit.request;/* # parse("File Header.java")*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ValidationException;

/**
 * File Name: ItemRequestValidator.java
 * Author: Marina Volkova
 * Date: 2023-08-27,   10:05 PM (UTC+3)
 * Description:
 */
@Component
@Slf4j
public class ItemRequestValidator {

    public void isValid(ItemRequest itemRequest) {
        itemRequestDescriptionValidator(itemRequest.getDescription());
    }

    private void itemRequestDescriptionValidator(String description) {
        if (!StringUtils.hasText(description)) {
            log.error("ItemRequestValidator: получено пустое описание или null.");
            throw new ValidationException("Описание не может быть пустым или null.");
        }
    }

}
