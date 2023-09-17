package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemResponseShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * File Name: BookingTest.java
 * Author: Marina Volkova
 * Date: 2023-09-17,   9:23 PM (UTC+3)
 * Description:
 */
@SpringBootTest
public class BookingTest {
    @Autowired
    BookingValidator validator;

    @Test
    public void createInvalidBookingsTest() {
        Collection<BookingDto> bookings = new ArrayList<>();

        //Отсутствие даты начала аренды
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(15));
        bookingDto.setItemId(1L);
        bookingDto.setItem(new ItemResponseShortDto(1L, "item1"));
        bookings.add(bookingDto);

        //Начало аренды в прошлом
        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setStart(LocalDateTime.now().minusMinutes(15));
        bookingDto2.setEnd(LocalDateTime.now().plusMinutes(15));
        bookingDto2.setItemId(1L);
        bookingDto2.setItem(new ItemResponseShortDto(1L, "item1"));
        bookings.add(bookingDto2);

        //Отсутствие даты окончания аренды
        BookingDto bookingDto3 = new BookingDto();
        bookingDto3.setStart(LocalDateTime.now().plusMinutes(15));
        bookingDto3.setItemId(1L);
        bookingDto3.setItem(new ItemResponseShortDto(1L, "item1"));
        bookings.add(bookingDto3);

        //Окончание аренды в прошлом
        BookingDto bookingDto4 = new BookingDto();
        bookingDto4.setStart(LocalDateTime.now().plusMinutes(15));
        bookingDto2.setEnd(LocalDateTime.now().minusMinutes(15));
        bookingDto4.setItemId(1L);
        bookingDto4.setItem(new ItemResponseShortDto(1L, "item1"));
        bookings.add(bookingDto4);

        bookings.forEach(x -> assertThrows(ValidationException.class,
                () -> validator.isValid(x)));
    }
}

