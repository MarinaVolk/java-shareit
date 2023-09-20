package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(ItemRequest itemRequest, Long requestorId);

    List<ItemRequestDto> findItemRequestsByRequestorId(Long requestorId);

    Page<ItemRequestDto> findItemRequestsByPages(Long requestorId, Integer from, Integer size);

    Boolean requestExistsById(Long requestId);

    ItemRequestDto getItemRequestById(Long requestId);

    List<ItemRequestDto> findAllRequestsByPages(Long requestorId, Integer from, Integer size);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);
}
