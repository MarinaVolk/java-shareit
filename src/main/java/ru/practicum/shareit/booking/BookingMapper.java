package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemResponseShortDto;
import ru.practicum.shareit.user.UserMapper;

import java.util.Optional;

/**
 * File Name: BookingMapper.java
 * Author: Marina Volkova
 * Date: 2023-07-22,   5:37 PM (UTC+3)
 * Description:
 */
@Component
public class BookingMapper {
    public static BookingDto toDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setId(booking.getId());

        ItemResponseShortDto item = new ItemResponseShortDto(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        bookingDto.setItem(item);

        bookingDto.setBooker(UserMapper.toDto(booking.getBooker()));

        return bookingDto;
    }

    public static Booking fromDto(BookingDto bookingDto) {
        Booking booking = new Booking();
        Optional.ofNullable(bookingDto.getId()).ifPresent(booking::setId);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());

        Item item = new Item();
        item.setId(bookingDto.getItem().getId());
        item.setName(bookingDto.getItem().getName());
        booking.setItem(item);

        booking.setBooker(UserMapper.fromDto(bookingDto.getBooker()));

        return booking;
    }
}
