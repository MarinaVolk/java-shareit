package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Sprint add-ru.practicum.shareit.item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                         @RequestBody ItemRequest itemRequest) {

        return service.addItemRequest(itemRequest, requestorId);
    }

    @GetMapping
    public List<ItemRequestDto> findAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long requestorId) {

        return service.findItemRequestsByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequestsByPages(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                                       @RequestParam(required = false, defaultValue = "0") Integer from,
                                                       @RequestParam(required = false, defaultValue = "20") Integer size) {

        return service.findAllRequestsByPages(requestorId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto findRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {

        return service.getItemRequestById(userId, requestId);
    }

}
