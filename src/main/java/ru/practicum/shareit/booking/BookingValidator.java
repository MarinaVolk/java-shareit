package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import lombok.extern.slf4j.Slf4j;
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

    public void isValid(Booking booking) throws ValidationException {
        startValidator(booking.getStart());
        endValidator(booking.getEnd());
        rentDateValidator(booking.getStart(), booking.getEnd());
    }

    private void startValidator(LocalDateTime start) throws ValidationException {
        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала аренды не может быть в прошлом.");
        }
    }

    private void endValidator(LocalDateTime end) throws ValidationException {
        if (end.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания аренды не может быть в прошлом.");
        }
    }

    private void rentDateValidator(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала аренды не может быть после даты окончания.");
        }
    }

}
