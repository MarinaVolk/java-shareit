package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.Data;
import ru.practicum.shareit.booking.BookingShortDto;
import ru.practicum.shareit.item.comments.CommentDto;

import java.util.List;

/**
 * File Name: ItemResponseFullDto.java
 * Author: Marina Volkova
 * Date: 2023-07-24,   10:12 PM (UTC+3)
 * Description:
 */
@Data
public class ItemResponseFullDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private List<CommentDto> comments;

}