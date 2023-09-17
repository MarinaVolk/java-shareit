package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * File Name: ItemTest.java
 * Author: Marina Volkova
 * Date: 2023-09-17,   9:03 PM (UTC+3)
 * Description:
 */
@SpringBootTest
public class ItemTest {
    @Autowired
    ItemValidator validator;

    @Test
    public void createInvalidItemsTest() {
        Collection<Item> items = new ArrayList<>();

        //Без названия
        Item item = new Item();
        item.setDescription("item1description");
        item.setAvailable(true);
        items.add(item);

        //Пустое название
        Item item2 = new Item();
        item2.setName("");
        item2.setDescription("item2description");
        item2.setAvailable(true);
        items.add(item2);

        //Без описания
        Item item3 = new Item();
        item3.setName("item3");
        item3.setAvailable(true);
        items.add(item3);

        //Пустое описание
        Item item4 = new Item();
        item4.setName("item4");
        item4.setDescription("");
        item4.setAvailable(true);
        items.add(item4);

        //Без поля о доступности
        Item item5 = new Item();
        item5.setName("item5");
        item5.setDescription("item5description");
        items.add(item5);

        items.forEach(x -> assertThrows(ValidationException.class, () -> validator.isValid(x)));
    }
}
