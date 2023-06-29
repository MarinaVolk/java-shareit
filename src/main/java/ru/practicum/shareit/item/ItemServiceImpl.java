package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.IncorrectOwnerId;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File Name: ItemServiceImpl.java
 * Author: Marina Volkova
 * Date: 2023-06-28,   10:02 PM (UTC+3)
 * Description:
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository = new ItemInMemoryRepository();
    InMemoryUserRepository userRepository = new InMemoryUserRepository();

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.fromDto(itemDto);

        item.setOwnerId(userId);
        item = itemRepository.addItem(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        Item updateItem = ItemMapper.fromDto(itemDto);
        Item oldItem = itemRepository.getItemById(itemId);

        if (!oldItem.getOwnerId().equals(userId)) {
            log.error("ItemService: вещь с id={} не принадлежит пользователю с id={}", itemId, userId);
            throw new IncorrectOwnerId("Нельзя редактировать не принадлежащую пользователю вещь.");
        }

        log.info("ItemService: вещь с id={} обновлена.", itemId);
        Item item = itemRepository.updateItem(updateItem);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        if (!itemRepository.itemIsContained(itemId)) {
            log.error("ItemService: Вещи с id={} в базе нет.", itemId);
            throw new NotFoundException("Такой вещи в базе нет.");
        }
        Item item = itemRepository.getItemById(itemId);
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItemsListByOwnerId(Long userId) {
        log.info("ItemService: запрос для получения списка вещей владельца с id={} ", userId);
        List<Item> itemsOfOwner = itemRepository.getItemListByOwnerId(userId);
        return itemsOfOwner.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        if (!StringUtils.hasText(text)) {
            log.info("ItemService: текст для поиска пустой, список пуст.");
            return new ArrayList<>();
        } else {
            log.info("ItemService: запрос для поиска вещей содержащих текст \"{}\"", text);
            return itemRepository.searchItemByText(text).stream()
                            .filter(item -> item.getAvailable().equals(true))
                    .map(ItemMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

}
