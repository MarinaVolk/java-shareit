package ru.practicum.shareit.booking;/* # parse("File Header.java")*/

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.validation.ConstraintViolationException;
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
    private final BookingMapper bookingMapper;
    private final BookingValidator bookingValidator;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public BookingDto addBooking(Booking booking, Long bookerId) {

        //Booking bookingCheck = bookingRepository.findByBookerIdAndItemId(bookerId, booking.getItemId());

        if (!userService.userExistsById(bookerId)) {
            throw new UserEmailAlreadyUsedException("Такого пользователя не существует.");
        }

        if (!itemService.itemExistsById(booking.getItemId())) {
            throw new NotFoundException("Такой вещи в базе нет.");
        }

        if (!itemService.getItemById(booking.getItemId()).getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования.");
        }

        /*if (bookingCheck != null && bookingCheck.getStatus() == BookingStatus.REJECTED) {
            throw new ValidationException("Повторное бронирование делать нельзя");
        }*/

        if (itemService.getItemById(booking.getItemId()).getOwnerId().equals(bookerId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь.");
        }

        booking.setBookerId(bookerId);
        booking.setStatus(BookingStatus.WAITING);

        bookingValidator.isValid(booking);

        // wrong user id exception
        booking = bookingRepository.save(booking);
        BookingDto bookingDto = bookingMapper.toDto(booking);
        bookingDto.getItem().setName(itemService.getItemById(bookingDto.getItem().getId()).getName());

        return bookingDto;
    }

    @Override
    public BookingDto getBookingByIdAndBookerId(Long bookingId, Long bookerId) {
        BookingDto bookingDto = getBookingById(bookingId);

        if (!bookingDto.getBooker().getId().equals(bookerId)
                && !itemService.getItemById(bookingDto.getItem().getId()).getOwnerId().equals(bookerId)) {
            log.error("Бронирование может получить только его создатель "
                    + "или владелец вещи для бронирования.");
            throw new IncorrectOwnerId("Нет прав доступа к бронированию.");
        }
        bookingDto.getItem().setName(itemService.getItemById(bookingDto.getItem().getId()).getName());
        return bookingDto;
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {

        if (!bookingRepository.existsById(bookingId)) {
            log.error("BookingService: Бронирования с id={} не существует", bookingId);
            throw new NotFoundException("Такого бронирования в базе нет.");
        }
        BookingDto bookingDto = bookingMapper.toDto(bookingRepository.getReferenceById(bookingId));
        bookingDto.getItem().setName(itemService.getItemById(bookingDto.getItem().getId()).getName());
        return bookingDto;
    }

    @Override
    public BookingDto updateBookingStatus(BookingDto bookingDto) {
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
    public Boolean bookingExists(Long itemId) {
        List<Booking> bookings = getBookingsByItemId(itemId);
        if (!bookings.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        BookingDto bookingDto = getBookingById(bookingId);

        if (!itemService.getItemById(bookingDto.getItem().getId()).getOwnerId().equals(ownerId)) {
            log.error("Подтвердить или отклонить бронирование может только владелец, "
                    + "пользователь с id={} не владеет вещью с id={}", ownerId, bookingId);
            throw new NotFoundException("Нет прав доступа к бронированию");
        }

        if (!bookingDto.getStatus().equals(BookingStatus.WAITING)) {
            log.error("Статус бронирования {} с id={} уже был изменён владельцем вещи.",
                    bookingDto.getStatus(), bookingId);
            throw new ValidationException("Запрос уже был обработан владельцем вещи");
        }

        if (approved) {
            bookingDto.setStatus(BookingStatus.APPROVED);
        } else {
            bookingDto.setStatus(BookingStatus.REJECTED);
        }

        bookingDto = updateBookingStatus(bookingDto);

        bookingDto.getItem().setName(itemService.getItemById(bookingDto.getItem().getId()).getName());
        return bookingDto;
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

        Comparator<Booking> endComparator = (o1, o2) -> {
            if (o1.getEnd().isBefore(o2.getEnd())) {
                return -1;
            } else if (o2.getEnd().isBefore(o1.getEnd())) {
                return 1;
            } else {
                return 0;
            }
        };

        Comparator<Booking> startComparator = (o1, o2) -> {
            if (o1.getStart().isBefore(o2.getStart())) {
                return 1;
            } else if (o1.getStart().isAfter(o2.getStart())) {
                return -1;
            } else {
                return 0;
            }
        };

        List<Booking> bookings = getBookingsByItemId(itemDtoForGet.getId());

        Optional<Booking> lastBooking = bookings.stream()
                .sorted(endComparator)
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now().plusHours(1)))
                .filter(x -> !x.getStatus().equals(BookingStatus.REJECTED))
                .max(endComparator); // changed findFirst()

        BookingForItem lastBookingForItem = new BookingForItem();

        if (lastBooking.isPresent()) {
            lastBookingForItem.setId(lastBooking.get().getId());
            lastBookingForItem.setBookerId(lastBooking.get().getBookerId());
            itemDtoForGet.setLastBooking(lastBookingForItem);
        }

//        bookings = getBookingsByItemId(itemDtoForGet.getId());

        Optional<Booking> nextBooking = bookings.stream()
                .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                .filter(x -> !x.getStatus().equals(BookingStatus.REJECTED))
                .min(endComparator);

        BookingForItem nextBookingForItem = new BookingForItem();

        if (nextBooking.isPresent()) {
            nextBookingForItem.setId(nextBooking.get().getId());
            nextBookingForItem.setBookerId(nextBooking.get().getBookerId());
            itemDtoForGet.setNextBooking(nextBookingForItem);
        }
        return itemDtoForGet;
    }


}
