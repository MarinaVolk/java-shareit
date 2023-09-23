package ru.practicum.shareit.request;/* # parse("File Header.java")*/

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * File Name: ItemRequestMapper.java
 * Author: Marina Volkova
 * Date: 2023-08-25,   10:45 PM (UTC+3)
 * Description:
 */
@Component
public class ItemRequestMapper {
    public ItemRequestDto toDto(ItemRequest itemRequest) {

        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestorId(itemRequest.getRequestor().getId());

        Optional.ofNullable(itemRequest.getId()).ifPresent(itemRequestDto::setId);

        Optional.ofNullable(itemRequest.getCreated()).ifPresent(itemRequestDto::setCreated);

        return itemRequestDto;
    }
}
