package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemResponseShortDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * File Name: BookingValidatorTest.java
 * Author: Marina Volkova
 * Date: 2023-09-17,   9:23 PM (UTC+3)
 * Description:
 */
@SpringBootTest
public class BookingValidatorTest {
    @Autowired
    private BookingValidator validator;

    @Test
    public void isValid_throwException_StartDateAbsent() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(15));
        bookingDto.setItemId(1L);
        bookingDto.setItem(new ItemResponseShortDto(1L, "item1"));

        assertThrows(ValidationException.class, () -> validator.isValid(bookingDto),
                "Ожидалось исключение, но не произошло."
        );
    }

    @Test
    public void isValid_throwException_StartDateIsInPast() {
        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setStart(LocalDateTime.now().minusMinutes(15));
        bookingDto2.setEnd(LocalDateTime.now().plusMinutes(15));
        bookingDto2.setItemId(1L);
        bookingDto2.setItem(new ItemResponseShortDto(1L, "item1"));

        assertThrows(ValidationException.class,
                () -> validator.isValid(bookingDto2),"Ожидалось исключение, но не произошло."
        );
    }


    @Test
    public void isValid_throwException_EndDateIsAbsent() {
        BookingDto bookingDto3 = new BookingDto();
        bookingDto3.setStart(LocalDateTime.now().plusMinutes(15));
        bookingDto3.setItemId(1L);
        bookingDto3.setItem(new ItemResponseShortDto(1L, "item1"));

        assertThrows(ValidationException.class,
                () -> validator.isValid(bookingDto3),"Ожидалось исключение, но не произошло."
        );
    }

    @Test
    public void isValid_throwException_EndDateIsInPast() {
        BookingDto bookingDto4 = new BookingDto();
        bookingDto4.setStart(LocalDateTime.now().plusMinutes(15));
        bookingDto4.setEnd(LocalDateTime.now().minusMinutes(15));
        bookingDto4.setItemId(1L);
        bookingDto4.setItem(new ItemResponseShortDto(1L, "item1"));

        assertThrows(ValidationException.class,
                () -> validator.isValid(bookingDto4),"Ожидалось исключение, но не произошло."
        );
    }
}

