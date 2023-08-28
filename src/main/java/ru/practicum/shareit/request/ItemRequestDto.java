package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private Long id;

    private String description;

    private Long requestorId;

    private LocalDateTime created = LocalDateTime.now();

    private List<ItemDto> items;  // maybe create ItemForItemRequestDto?
}
