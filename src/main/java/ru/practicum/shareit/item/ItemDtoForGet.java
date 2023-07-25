package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.Data;

import java.util.List;

/**
 * File Name: ItemDtoForGet.java
 * Author: Marina Volkova
 * Date: 2023-07-24,   10:12 PM (UTC+3)
 * Description:
 */
@Data
public class ItemDtoForGet {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingForItem lastBooking;

    private BookingForItem nextBooking;

    private List<CommentDto> comments;

}