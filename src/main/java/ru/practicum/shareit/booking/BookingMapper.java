package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * File Name: BookingMapper.java
 * Author: Marina Volkova
 * Date: 2023-07-22,   5:37 PM (UTC+3)
 * Description:
 */
@Component
public class BookingMapper {
    public BookingDto toDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(new ItemForResponse(booking.getItemId()));
        bookingDto.setBooker(new Booker(booking.getBookerId()));
        Optional.ofNullable(booking.getId()).ifPresent(bookingDto::setId);
        return bookingDto;
    }

    public Booking fromDto(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setBookerId(bookingDto.getBooker().getId());
        booking.setItemId(bookingDto.getItem().getId());
        booking.setItemName(bookingDto.getItem().getName());
        booking.setStatus(bookingDto.getStatus());
        Optional.ofNullable(bookingDto.getId()).ifPresent(booking::setId);
        return booking;
    }
}
