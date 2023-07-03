package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody ItemDto itemDto) {

        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto, @PathVariable Long itemId) {

        log.info("ItemController: запрос на обновление данных о вещи {} с id={} ", itemDto.getName(), itemId);
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("ItemController: запрос на получение данных о вещи с id={} ", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsListByOwnerId(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "null") Long userId) {
        log.info("ItemController.getItemsListByOwnerId: Получен запрос на список вещей пользователя с id={}", userId);
        return itemService.getItemsListByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam String text) {
        log.info("ItemController: запрос на поиск доступных к аренде вещей с текстом \"{}\" ", text);
        return itemService.searchItemByText(text);
    }

}
