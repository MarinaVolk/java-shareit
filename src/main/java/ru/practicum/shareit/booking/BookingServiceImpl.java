package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File Name: BookingServiceImpl.java
 * Author: Marina Volkova
 * Date: 2023-07-22,   2:08 PM (UTC+3)
 * Description:
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingValidator bookingValidator;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public BookingDto addBooking(Booking booking, Long bookerId) {

        Booking bookingCheck = bookingRepository.findByBookerIdAndItemId(bookerId, booking.getItemId());

        if (bookingCheck != null && bookingCheck.getState() == BookingState.REJECTED) {
            throw new ValidationException("Повторное бронирование делать нельзя");
        }

        booking.setBookerId(bookerId);
        booking.setState(BookingState.WAITING);

        bookingValidator.isValid(booking);

        booking = bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {

        if (!bookingRepository.existsById(bookingId)) {
            log.error("BookingService: Бронирования с id={} не существует", bookingId);
            throw new NotFoundException("Такого бронирования в базе нет.");
        }
        return bookingMapper.toDto(bookingRepository.getReferenceById(bookingId));
    }

    @Override
    public BookingDto updateBookingState(BookingDto bookingDto) {
        return bookingMapper.toDto(bookingRepository.save(bookingMapper.fromDto(bookingDto)));
    }

    @Override
    public List<BookingDto> getBookingsByBookerId(Long bookerId) {
        return bookingRepository.findByBookerId(bookerId).stream()
                .map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByIdItemsList(List<Long> itemsId) {
        return bookingRepository.findByItemIdIn(itemsId).stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<Booking> getBookingsByItemId(Long itemId) {
        return bookingRepository.findByItemId(itemId);
    }

    @Override
    public BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        BookingDto bookingDto = getBookingById(bookingId);

        if (!itemService.getItemById(bookingDto.getItem().getId()).getOwnerId().equals(ownerId)) {
            log.error("Подтвердить или отклонить бронирование может только владелец, "
                    + "пользователь с id={} не владеет вещью с id={}", ownerId, bookingId);
            throw new NotFoundException("Нет прав доступа к бронированию");
        }

        if (!bookingDto.getState().equals(BookingState.WAITING)) {
            log.error("Статус бронирования {} с id={} уже был изменён владельцем вещи.",
                    bookingDto.getState(), bookingId);
            throw new ValidationException("Запрос уже был обработан владельцем вещи");
        }

        if (approved) {
            bookingDto.setState(BookingState.APPROVED);
        } else {
            bookingDto.setState(BookingState.REJECTED);
        }

        bookingDto = updateBookingState(bookingDto);

        bookingDto.getItem().setName(itemService.getItemById(bookingDto.getItem().getId()).getName());

        return bookingDto;

    }

    @Override
    public List<BookingDto> getAllBookingsByBookerIdDesc(Long bookerId, String state) {

        if (!userService.userExistsById(bookerId)) {
            log.error("Пользователя с id={} в базе нет", bookerId);
            throw new NotFoundException("Такого пользователя в базе нет.");
        }

        List<BookingDto> bookingsByBookerId = getBookingsByBookerId(bookerId);
        return getBookingDtos(state, bookingsByBookerId);
    }

    @Override
    public List<BookingDto> getAllBookingsByItemOwnerId(Long ownerId, String state) {

        if (!userService.userExistsById(ownerId)) {
            log.error("Пользователя с id={} в базе нет.", ownerId);
            throw new NotFoundException("Такого пользователя в базе нет");
        }

        List<ItemDto> itemsByOwnerId = itemService.getItemsListByOwnerId(ownerId);

        if (itemsByOwnerId.size() == 0) {
            return new ArrayList<>();
        }

        List<Long> itemsIdByOwnerId = itemsByOwnerId.stream()
                .map(ItemDto::getId)
                .collect(Collectors.toList());

        List<BookingDto> bookingsByItemOwnerId = getBookingsByIdItemsList(itemsIdByOwnerId);

        return getBookingDtos(state, bookingsByItemOwnerId);

    }

    private List<BookingDto> getBookingDtos(String state, List<BookingDto> bookingsByItemOwnerId) {

        Comparator<BookingDto> comparator = (o1, o2) -> {
            if (o1.getStart().isBefore(o2.getStart())) {
                return 1;
            } else if (o1.getStart().isAfter(o2.getStart())) {
                return -1;
            } else {
                return 0;
            }
        };

        switch (state) {
            case "CURRENT":
                return bookingsByItemOwnerId.stream()
                        .filter(x -> (x.getStart().isBefore(LocalDateTime.now()) && x.getEnd().isAfter(LocalDateTime.now())))
                        .peek(x -> x.getItem().setName(itemService.getItemById(x.getItem().getId()).getName()))
                        .sorted(comparator)
                        .collect(Collectors.toList());

            case "PAST":
                return bookingsByItemOwnerId.stream()
                        .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                        .peek(x -> x.getItem().setName(itemService.getItemById(x.getItem().getId()).getName()))
                        .sorted(comparator)
                        .collect(Collectors.toList());

            case "FUTURE":
                return bookingsByItemOwnerId.stream()
                        .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                        .peek(x -> x.getItem().setName(itemService.getItemById(x.getItem().getId()).getName()))
                        .sorted(comparator)
                        .collect(Collectors.toList());

            case "WAITING":
                return bookingsByItemOwnerId.stream()
                        .filter(x -> x.getState().equals(BookingState.WAITING))
                        .peek(x -> x.getItem().setName(itemService.getItemById(x.getItem().getId()).getName()))
                        .sorted(comparator)
                        .collect(Collectors.toList());

            case "REJECTED":
                return bookingsByItemOwnerId.stream()
                        .filter(x -> x.getState().equals(BookingState.REJECTED))
                        .peek(x -> x.getItem().setName(itemService.getItemById(x.getItem().getId()).getName()))
                        .sorted(comparator)
                        .collect(Collectors.toList());

            case "ALL":
                return bookingsByItemOwnerId.stream()
                        .peek(x -> x.getItem().setName(itemService.getItemById(x.getItem().getId()).getName()))
                        .sorted(comparator)
                        .collect(Collectors.toList());

            default:
                log.error("Неверный параметр state={}", state);
                throw new ValidationException("Unknown state: " + state);
        }
    }

}
