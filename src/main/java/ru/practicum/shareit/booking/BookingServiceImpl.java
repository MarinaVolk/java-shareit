package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    private final BookingValidator bookingValidator;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public BookingDto addBooking(BookingDto booking, Long bookerId) {
        UserDto booker = userService.getUserById(bookerId);
        ItemDto item = itemService.getItemById(booking.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования.");
        }

        if (item.getOwnerId().equals(bookerId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь.");
        }
        bookingValidator.isValid(booking);

        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        ItemForResponseDto itemForResponseDto = new ItemForResponseDto(item.getId());
        itemForResponseDto.setName(item.getName());
        booking.setItem(itemForResponseDto);

        Booking bookingForRepository = BookingMapper.fromDto(booking);
        booking = BookingMapper.toDto(bookingRepository.save(bookingForRepository));
        return booking;
    }

    @Override
    public BookingDto getBookingByIdAndBookerId(Long bookingId, Long bookerId) {
        BookingDto bookingDto = getBookingById(bookingId);

        ItemDto item = itemService.getItemById(bookingDto.getItem().getId());

        if (!bookingDto.getBooker().getId().equals(bookerId)
                && !item.getOwnerId().equals(bookerId)) {
            log.error("Бронирование может получить только его создатель "
                    + "или владелец вещи для бронирования.");
            throw new IncorrectOwnerId("Нет прав доступа к бронированию.");
        }
        bookingDto.getItem().setName(item.getName());
        return bookingDto;
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {

        if (!bookingRepository.existsById(bookingId)) {
            log.error("BookingService: Бронирования с id={} не существует", bookingId);
            throw new NotFoundException("Такого бронирования в базе нет.");
        }
        BookingDto bookingDto = BookingMapper.toDto(bookingRepository.getReferenceById(bookingId));
        bookingDto.getItem().setName(itemService.getItemById(bookingDto.getItem().getId()).getName());
        return bookingDto;
    }

    @Override
    public BookingDto updateBookingStatus(BookingDto bookingDto) {
        return BookingMapper.toDto(bookingRepository.save(BookingMapper.fromDto(bookingDto)));
    }

    @Override
    public List<BookingDto> getBookingsByBookerId(Long bookerId) {
        List<Booking> bookings = bookingRepository.findByBookerId(bookerId);
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(BookingMapper.toDto(booking));
        }
        return bookingDtos;
    }

    @Override
    public List<BookingDto> getBookingsByIdItemsList(List<Long> itemsId) {
        List<Booking> bookings = bookingRepository.findByItemIdIn(itemsId);
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(BookingMapper.toDto(booking));
        }
        return bookingDtos;
    }

    @Override
    public List<BookingDto> getBookingsByItemId(Long itemId) {
        List<Booking> bookings = bookingRepository.findByItemId(itemId);
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(BookingMapper.toDto(booking));
        }
        return bookingDtos;
    }

    @Override
    public Boolean bookingExists(Long itemId) {
        List<BookingDto> bookings = getBookingsByItemId(itemId);
        if (!bookings.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId +
                    " не является владельцем вещи с id " + booking.getItem().getId());
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Нельзя изменить статус подтвержденного бронирования");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getAllBookingsByBookerIdDesc(Long bookerId, String status) {

        if (!userService.userExistsById(bookerId)) {
            log.error("Пользователя с id={} в базе нет", bookerId);
            throw new NotFoundException("Такого пользователя в базе нет.");
        }

        List<BookingDto> bookingsByBookerId = getBookingsByBookerId(bookerId);
        return getBookingDtos(status, bookingsByBookerId);
    }

    @Override
    public List<BookingDto> getAllBookingsByItemOwnerId(Long ownerId, String status) {

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

        return getBookingDtos(status, bookingsByItemOwnerId);

    }

    private List<BookingDto> getBookingDtos(String status, List<BookingDto> bookingsByItemOwnerId) {

        Comparator<BookingDto> comparator = (o1, o2) -> {
            if (o1.getStart().isBefore(o2.getStart())) {
                return 1;
            } else if (o1.getStart().isAfter(o2.getStart())) {
                return -1;
            } else {
                return 0;
            }
        };

        switch (status) {
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
                        .filter(x -> x.getStatus().equals(BookingStatus.WAITING))
                        .peek(x -> x.getItem().setName(itemService.getItemById(x.getItem().getId()).getName()))
                        .sorted(comparator)
                        .collect(Collectors.toList());

            case "REJECTED":
                return bookingsByItemOwnerId.stream()
                        .filter(x -> x.getStatus().equals(BookingStatus.REJECTED))
                        .peek(x -> x.getItem().setName(itemService.getItemById(x.getItem().getId()).getName()))
                        .sorted(comparator)
                        .collect(Collectors.toList());

            case "ALL":
                return bookingsByItemOwnerId.stream()
                        .peek(x -> x.getItem().setName(itemService.getItemById(x.getItem().getId()).getName()))
                        .sorted(comparator)
                        .collect(Collectors.toList());

            default:
                log.error("Неверный параметр status={}", status);
                throw new UnSupportedStatusException("Unknown state: " + status);
        }
    }

    public ItemDtoForGet setLastAndNextBooking(ItemDtoForGet itemDtoForGet) {

        Comparator<BookingDto> endComparator = (o1, o2) -> {
            if (o1.getEnd().isBefore(o2.getEnd())) {
                return -1;
            } else if (o2.getEnd().isBefore(o1.getEnd())) {
                return 1;
            } else {
                return 0;
            }
        };

        Comparator<BookingDto> startComparator = (o1, o2) -> {
            if (o1.getStart().isBefore(o2.getStart())) {
                return 1;
            } else if (o1.getStart().isAfter(o2.getStart())) {
                return -1;
            } else {
                return 0;
            }
        };

        List<BookingDto> bookings = getBookingsByItemId(itemDtoForGet.getId());

        Optional<BookingDto> lastBooking = bookings.stream()
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now().plusHours(1)))
                .filter(x -> !x.getStatus().equals(BookingStatus.REJECTED))
                .max(endComparator);

        BookingForItem lastBookingForItem = new BookingForItem();

        if (lastBooking.isPresent()) {
            lastBookingForItem.setId(lastBooking.get().getId());
            lastBookingForItem.setBookerId(lastBooking.get().getBooker().getId());
            itemDtoForGet.setLastBooking(lastBookingForItem);
        }

        Optional<BookingDto> nextBooking = bookings.stream()
                .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                .filter(x -> !x.getStatus().equals(BookingStatus.REJECTED))
                .min(endComparator);

        BookingForItem nextBookingForItem = new BookingForItem();

        if (nextBooking.isPresent()) {
            nextBookingForItem.setId(nextBooking.get().getId());
            nextBookingForItem.setBookerId(nextBooking.get().getBooker().getId());
            itemDtoForGet.setNextBooking(nextBookingForItem);
        }
        return itemDtoForGet;
    }

}
