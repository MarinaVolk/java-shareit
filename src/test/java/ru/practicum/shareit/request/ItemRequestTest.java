package ru.practicum.shareit.request;/* # parse("File Header.java")*/

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * File Name: ItemRequestTest.java
 * Author: Marina Volkova
 * Date: 2023-09-18,   12:10 AM (UTC+3)
 * Description:
 */
@SpringBootTest
public class ItemRequestTest {
    @Autowired
    ItemRequestValidator validator;

    @Test
    public void createInvalidItemRequestsTest() {
        Collection<ItemRequest> requests = new ArrayList<>();

        //Без описания
        ItemRequest itemRequest = new ItemRequest();
        requests.add(itemRequest);

        //Пустое описание
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("");
        requests.add(itemRequest1);

        requests.forEach(x -> assertThrows(ValidationException.class,
                () -> validator.isValid(x)));
    }
}
