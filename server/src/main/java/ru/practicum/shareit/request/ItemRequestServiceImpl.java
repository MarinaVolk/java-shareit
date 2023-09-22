package ru.practicum.shareit.request;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.PageUtil.*;

/**
 * File Name: ItemRequestServiceImpl.java
 * Author: Marina Volkova
 * Date: 2023-08-27,   6:03 PM (UTC+3)
 * Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public ItemRequestDto addItemRequest(ItemRequest itemRequest, Long requestorId) {

        UserDto requestor = userService.getUserById(requestorId);
        User requestorForItem = UserMapper.fromDto(requestor);

        itemRequest.setRequestor(requestorForItem);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findItemRequestsByRequestorId(Long requestorId) {
        if (!userService.userExistsById(requestorId)) {
            log.error("Пользователя с id={} в базе нет", requestorId);
            throw new NotFoundException("Такого пользователя в базе нет.");
        }
        return itemRequestRepository.findItemRequestsByRequestorIdOrderByCreatedDesc(requestorId)
                .stream()
                .map(itemRequestMapper::toDto)
                .peek(x -> x.setItems(itemService.getItemsForItemRequestDtoByRequestId(x.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ItemRequestDto> findItemRequestsByPages(Long requestorId, Integer from, Integer size) {

        Pageable pageable = createPage(from, size, "created");
        return itemRequestRepository.findAllByRequestorIdNot(requestorId, pageable)
                .map(itemRequestMapper::toDto);
    }

    @Override
    public Boolean requestExistsById(Long requestId) {
        return itemRequestRepository.existsById(requestId);
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId) {
        if (!requestExistsById(requestId)) {
            log.error("Запроса с id={} в базе нет.", requestId);
            throw new NotFoundException("Запроса в базе нет.");
        }
        return itemRequestMapper.toDto(itemRequestRepository.getReferenceById(requestId));
    }

    @Override
    public List<ItemRequestDto> findAllRequestsByPages(Long requestorId, Integer from, Integer size) {
        if (!userService.userExistsById(requestorId)) {
            log.error("Пользователя с id={} в базе нет", requestorId);
            throw new NotFoundException("Такого пользователя в базе нет.");
        }

        if (size < 1 || from < 0) {
            log.error("Параметр size={} или from={} неверны", size, from);
            throw new ValidationException("Параметр size или from некорректный");
        }
        return findItemRequestsByPages(requestorId, from, size).getContent()
                .stream()
                .peek(x -> x.setItems(itemService.getItemsForItemRequestDtoByRequestId(x.getId()))) // ???????
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        if (!userService.userExistsById(userId)) {
            log.error("Пользователя с id={} в базе нет", userId);
            throw new NotFoundException("Такого пользователя в базе нет.");
        }
        ItemRequestDto itemRequestDto = getItemRequestById(requestId);
        itemRequestDto.setItems(itemService.getItemsForItemRequestDtoByRequestId(requestId));
        return itemRequestDto;
    }
}
