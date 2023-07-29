package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;

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
    public ItemDtoForGet getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long itemId) {
        log.info("ItemController: запрос на получение данных о вещи с id={} ", itemId);

        ItemDto itemDto = itemService.getItemById(itemId);
        ItemDtoForGet itemDtoForGet = itemService.getItemByIdForGet(itemId);

        if (!itemDto.getOwnerId().equals(userId)) {
            itemDtoForGet.setLastBooking(null);
            itemDtoForGet.setNextBooking(null);
        } else {
            if (bookingService.bookingExists(itemId)) {
                bookingService.setLastAndNextBooking(itemDtoForGet);
            }
        }
        return itemDtoForGet;
    }

    @GetMapping
    public List<ItemDtoForGet> getItemsListByOwnerId(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "null") Long userId) {
        log.info("ItemController.getItemsListByOwnerId: Получен запрос на список вещей пользователя с id={}", userId);

        List<ItemDto> itemsList = itemService.getItemsListByOwnerId(userId);
        List<ItemDtoForGet> itemsForGet = new ArrayList<>();

        for (ItemDto itemDto : itemsList) {
            ItemDtoForGet itemDtoForGet = itemService.getItemByIdForGet(itemDto.getId());
            bookingService.setLastAndNextBooking(itemDtoForGet);
            itemsForGet.add(itemDtoForGet);
        }
        return itemsForGet;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam String text) {
        log.info("ItemController: запрос на поиск доступных к аренде вещей с текстом \"{}\" ", text);
        return itemService.searchItemByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                       @PathVariable Long itemId,
                                       @RequestBody Comment comment) {

        log.info("ItemController: запрос на добавление комментария к вещи с id={}", itemId);

        Optional<BookingDto> booking = bookingService.getBookingsByItemId(itemId).stream()
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                .filter(x -> x.getBooker().getId().equals(authorId))
                .findFirst();

        if (booking.isEmpty()) {
            throw new ValidationException("Пользователь не брал вещь в аренду.");
        }
        return itemService.addComment(comment, itemId, authorId);
    }

}
