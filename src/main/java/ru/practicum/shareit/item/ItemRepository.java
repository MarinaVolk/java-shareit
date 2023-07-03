package ru.practicum.shareit.item;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);

    Item getItemById(Long itemId);

    Item updateItem(Item item);

    Boolean itemIsContained(Long itemId);

    List<Item> getAll();

    List<Item> getItemListByOwnerId(Long userId);

    List<Item> searchItemByText(String text);
}
