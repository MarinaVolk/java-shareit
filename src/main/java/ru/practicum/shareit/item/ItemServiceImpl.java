package ru.practicum.shareit.item;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.IncorrectOwnerId;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemValidator validator;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.fromDto(itemDto);

        validator.isValid(item);
        if /*(userRepository.getUserById(userId) == null)*/ (!userRepository.existsById(userId)) {
            throw new NotFoundException("Такого пользователя в базе нет.");
        }
        item.setOwnerId(userId);
        item = itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        Item updateItem = ItemMapper.fromDto(itemDto);
        Item oldItem = itemRepository.getReferenceById(itemId);

        if (!oldItem.getOwnerId().equals(userId)) {
            log.error("ItemService: вещь с id={} не принадлежит пользователю с id={}", itemId, userId);
            throw new IncorrectOwnerId("Нельзя редактировать не принадлежащую пользователю вещь.");
        }

        log.info("ItemService: вещь с id={} обновлена.", itemId);
        Item item = itemUpdate(updateItem, oldItem);
        item.setId(itemId);
        item = itemRepository.save(item);
        return ItemMapper.toDto(item);
    }


    @Override
    public ItemDto getItemById(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            log.error("ItemService: Вещи с id={} в базе нет.", itemId);
            throw new NotFoundException("Такой вещи в базе нет.");
        }
        Item item = itemRepository.getReferenceById(itemId);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDtoForGet getItemByIdAndUserId(Long itemId, Long userId) {
        ItemDto itemDto = getItemById(itemId);
        ItemDtoForGet itemDtoForGet = ItemMapper.toDtoForGet(itemDto);

        List<Comment> comments = getCommentsByItemId(itemId);
        List<CommentDto> commentDtos = new ArrayList<>();

        for (Comment comment: comments) {
            Long authorId = comment.getAuthorId();
            User author = userRepository.getReferenceById(authorId);
            String authorName = author.getName();
            CommentDto commentDto = CommentMapper.toDto(comment);
            commentDto.setAuthorName(authorName);
            commentDtos.add(commentDto);
        }
        itemDtoForGet.setComments(commentDtos);

        return itemDtoForGet;

    }



    @Override
    public List<ItemDto> getItemsListByOwnerId(Long userId) {
        log.info("ItemService: запрос для получения списка вещей владельца с id={} ", userId);
        List<Item> itemsOfOwner = itemRepository.findItemsByOwnerId(userId);
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
            return itemRepository.searchItemForRentByText(text).stream()
                    .filter(item -> item.getAvailable().equals(true))
                    .map(ItemMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto addComment(Comment comment, Long itemId, Long authorId) {
        if (!StringUtils.hasText(comment.getText())) {
            throw new ValidationException("Текст комментария пустой.");
        }

        /*
        if (!itemExistsById(itemId)) {
            throw new NotFoundException("Такой вещи в базе нет.");
        }*/

        comment.setItemId(itemId);
        comment.setAuthorId(authorId);
        comment = commentRepository.save(comment);

        User author = userRepository.getReferenceById(authorId);
        String authorName = author.getName();

        CommentDto commentDto = CommentMapper.toDto(comment);
        commentDto.setAuthorName(authorName);

        return commentDto;
    }

    @Override
    public List<Comment> getCommentsByItemId(Long itemId) {
        return commentRepository.findCommentsByItemId(itemId);
    }

    @Override
    public Boolean itemExistsById(Long itemId) {
        return itemRepository.existsById(itemId);
    }

    private Item itemUpdate(Item updateItem, Item oldItem) {
        Item item = new Item();

        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        } else {
            item.setName(oldItem.getName());
        }

        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        } else {
            item.setDescription(oldItem.getDescription());
        }

        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        } else {
            item.setAvailable(oldItem.getAvailable());
        }

        if (updateItem.getOwnerId() != null) {
            item.setOwnerId(updateItem.getOwnerId());
        } else {
            item.setOwnerId(oldItem.getOwnerId());
        }
        return item;
    }




}
