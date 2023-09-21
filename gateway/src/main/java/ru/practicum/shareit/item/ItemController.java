package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * File Name: ItemController.java
 * Author: Marina Volkova
 * Date: 2023-09-21,   7:56 PM (UTC+3)
 * Description:
 */
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody @Valid ItemGatewayDto itemGatewayDto) {
        return itemClient.addItem(itemGatewayDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemGatewayDto itemGatewayDto) {
        return itemClient.updateItem(itemGatewayDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsListByOwnerId(
            @RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "-1") Long userId,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {

        return itemClient.getItemsListByOwnerId(userId, from, size);

    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemForRentByText(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(name = "text") String text,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {

        return itemClient.searchItemForRentByText(userId, text, from, size);

    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @PathVariable Integer itemId,
                                             @RequestBody @Valid CommentGatewayDto commentGatewayDto) {

        return itemClient.addComment(userId, itemId, commentGatewayDto);
    }
}
