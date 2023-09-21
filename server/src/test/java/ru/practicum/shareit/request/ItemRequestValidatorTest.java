package ru.practicum.shareit.request;/* # parse("File Header.java")*/

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * File Name: ItemRequestValidatorTest.java
 * Author: Marina Volkova
 * Date: 2023-09-18,   12:10 AM (UTC+3)
 * Description:
 */
@SpringBootTest
public class ItemRequestValidatorTest {
    @Autowired
    private ItemRequestValidator validator;

    @Test
    public void isValid_throwException_RequestDescriptionIsAbsent() {
        ItemRequest itemRequest = new ItemRequest();

        assertThrows(ValidationException.class, () -> validator.isValid(itemRequest),
                "Ожидалось исключение, но не произошло."
        );
    }

    @Test
    public void isValid_throwException_RequestDescriptionIsEmpty() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("");

        assertThrows(ValidationException.class, () -> validator.isValid(itemRequest),
                "Ожидалось исключение, но не произошло."
        );
    }
}
