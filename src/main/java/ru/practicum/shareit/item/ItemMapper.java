package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

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
        itemDto.setId(item.getId());
        Long userId = item.getOwner().getId();
        itemDto.setOwnerId(userId);
        Optional.ofNullable(item.getRequestId()).ifPresent(itemDto::setRequestId);
        return itemDto;
    }

    public static Item fromDto(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        Optional.ofNullable(itemDto.getId()).ifPresent(item::setId);
        User user = new User();
        Long userId = itemDto.getOwnerId();
        user.setId(userId);
        item.setOwner(user);
        Optional.ofNullable(itemDto.getRequestId()).ifPresent(item::setRequestId);
        return item;
    }

    public static ItemResponseFullDto toDtoForGet(ItemDto itemDto) {

        ItemResponseFullDto itemDtoForGetItems = new ItemResponseFullDto();

        itemDtoForGetItems.setId(itemDto.getId());
        itemDtoForGetItems.setName(itemDto.getName());
        itemDtoForGetItems.setDescription(itemDto.getDescription());
        itemDtoForGetItems.setAvailable(itemDto.getAvailable());
        return itemDtoForGetItems;
    }

    /*public ItemRequestDto toItemDtoForItemRequestDto(Item item) {

        ItemForItemRequestDto itemForItemRequestDto = new ItemForItemRequestDto();

        itemForItemRequestDto.setId(item.getId());
        itemForItemRequestDto.setName(item.getName());
        itemForItemRequestDto.setDescription(item.getDescription());
        itemForItemRequestDto.setOwnerId(item.getOwnerId());
        itemForItemRequestDto.setAvailable(item.getAvailable());

        Optional.ofNullable(item.getRequestId()).ifPresent(itemForItemRequestDto::setRequestId);

        return itemForItemRequestDto;

    } */

}
