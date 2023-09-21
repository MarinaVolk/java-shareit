package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

/**
 * File Name: BookingValidator.java
 * Author: Marina Volkova
 * Date: 2023-07-22,   5:51 PM (UTC+3)
 * Description:
 */
@Component
public class BookingValidator {

    public void isValid(BookingDto booking) throws ValidationException {
        if ((booking.getStart() == null) || (booking.getEnd() == null)) {
            throw new ValidationException("Даты начала аренды и окончания аренды должны быть заданы.");
        }
        validateStart(booking.getStart());
        validateEnd(booking.getEnd());
        validateRent(booking.getStart(), booking.getEnd());
    }

    private void validateStart(LocalDateTime start) throws ValidationException {
        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала аренды не может быть в прошлом.");
        }
    }

    private void validateEnd(LocalDateTime end) throws ValidationException {
        if (end.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания аренды не может быть в прошлом.");
        }
    }

    private void validateRent(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || (start.isEqual(end))) {
            throw new ValidationException("Дата начала аренды не может быть после даты окончания.");
        }
    }

}