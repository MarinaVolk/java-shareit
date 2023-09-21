package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * File Name: ItemValidatorTest.java
 * Author: Marina Volkova
 * Date: 2023-09-17,   9:03 PM (UTC+3)
 * Description:
 */
@SpringBootTest
public class ItemValidatorTest {
    @Autowired
    private ItemValidator validator;

    @Test
    public void isValid_throwException_ItemNameIsAbsent() {
        Item item = new Item();
        item.setDescription("item1description");
        item.setAvailable(true);

        assertThrows(ValidationException.class, () -> validator.isValid(item),
                "Ожидалось исключение, но не произошло."
        );
    }

    @Test
    public void isValid_throwException_ItemNameIsEmpty() {
        Item item = new Item();
        item.setName("");
        item.setDescription("item1description");
        item.setAvailable(true);

        assertThrows(ValidationException.class, () -> validator.isValid(item),
                "Ожидалось исключение, но не произошло."
        );
    }

    @Test
    public void isValid_throwException_ItemDescriptionIsAbsent() {
        Item item = new Item();
        item.setName("ru.practicum.shareit.item");
        item.setAvailable(true);

        assertThrows(ValidationException.class, () -> validator.isValid(item),
                "Ожидалось исключение, но не произошло."
        );
    }

    @Test
    public void isValid_throwException_ItemDescriptionIsEmpty() {
        Item item = new Item();
        item.setName("ru.practicum.shareit.item");
        item.setDescription("");
        item.setAvailable(true);

        assertThrows(ValidationException.class, () -> validator.isValid(item),
                "Ожидалось исключение, но не произошло."
        );
    }

    @Test
    public void isValid_throwException_ItemAvailabilityIsAbsent() {
        Item item = new Item();
        item.setName("ru.practicum.shareit.item");
        item.setDescription("itemDescription");

        assertThrows(ValidationException.class, () -> validator.isValid(item),
                "Ожидалось исключение, но не произошло."
        );
    }
}
