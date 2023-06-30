package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);
    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);
    ItemDto getItemById(Long itemId);
    List<ItemDto> getItemsListByOwnerId(Long userId);
    List<ItemDto> searchItemByText(String text);
}
