package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * File Name: ItemMapper.java
 * Author: Marina Volkova
 * Date: 2023-06-28,   7:38 PM (UTC+3)
 * Description:
 */
@Component
public class ItemMapper {
    public static ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        Optional.ofNullable(item.getId()).ifPresent(itemDto::setId);
        Optional.ofNullable(item.getOwnerId()).ifPresent(itemDto::setOwnerId);
        Optional.ofNullable(item.getRequestId()).ifPresent(itemDto::setRequestId);
        return itemDto;
    }

    public static Item fromDto(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        Optional.ofNullable(itemDto.getId()).ifPresent(item::setId);
        Optional.ofNullable(itemDto.getOwnerId()).ifPresent(item::setOwnerId);
        Optional.ofNullable(itemDto.getRequestId()).ifPresent(item::setRequestId);
        return item;
    }
}
