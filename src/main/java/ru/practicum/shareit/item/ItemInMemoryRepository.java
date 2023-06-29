package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File Name: ItemInMemoryRepository.java
 * Author: Marina Volkova
 * Date: 2023-06-29,   6:08 PM (UTC+3)
 * Description:
 */
@Repository
@Slf4j
@Qualifier("ItemRepository")
public class ItemInMemoryRepository implements ItemRepository {
    private final HashMap<Long, Item> items = new HashMap<>();
    private Long itemId = 0L;

    @Override
    public Item addItem(Item item) {
        item.setId(++itemId);
        items.put(itemId, item);
        log.info("ItemRepository: Вещь {} добавлена", item.getName());
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            log.error("ItemRepository: вещи с id={} в базе нет", itemId);
            throw new NotFoundException("Такой вещи в базе нет.");
        }
        return items.get(itemId);
    }

    @Override
    public Item updateItem(Item item) {
        if (!items.containsKey(itemId)) {
            log.error("ItemRepository: вещи с id={} в базе нет", item.getId());
            throw new NotFoundException("Такой вещи в базе нет.");
        }
        items.put(item.getId(), item);
        log.info("ItemRepository: вещь {} обновлена", item.getName());
        return item;
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getItemListByOwnerId(Long userId){
        log.info("ItemRepository: запрос списка вещей пользователя с id={} ", userId);
        return getAll().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItemByText(String text) {
        log.info("ItemRepository: запрос списка вещей по запросу \"{}\" ", text);
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean itemIsContained(Long itemId) {
        return items.containsKey(itemId);
    }

}
