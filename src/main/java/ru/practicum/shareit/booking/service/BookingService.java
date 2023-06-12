package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.reference.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import static ru.practicum.shareit.booking.mapper.BookingMapper.*;

@Service
public class BookingService {
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingService(BookingRepository repository, UserRepository userRepository, ItemRepository itemRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public BookingDtoResponse createBooking(BookingDto bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ид %s не найдена", bookingDto.getItemId())));
        if (item.getOwner().getId() == user.getId()) {
            throw new NotFoundException(String.format("Вещь с ид %s не найдена", bookingDto.getItemId()));
        }
        if (!item.getAvailable().booleanValue()) {
            throw new ValidationException(String.format("Вещь с ид %s не доступна для бронирования", bookingDto.getItemId()));
        }
        if (repository.findByStartDateBeforeEndDateAfter(bookingDto.getItemId(), bookingDto.getStart(), bookingDto.getStart()).size() > 0) {
            throw new ValidationException(String.format("Вещь с ид %s уже забронирована", bookingDto.getItemId()));
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException(String.format("Период не должен быть в прошлом: %s - %s", bookingDto.getStart(), bookingDto.getEnd()));
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException(String.format("Некорретный период: %s - %s", bookingDto.getStart(), bookingDto.getEnd()));
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        return toBookingDtoResponse(repository.save(toBooking(bookingDto, item, user)));
    }

    @Transactional
    public BookingDtoResponse requestApprovReject(Long id, Long userId, Boolean approved) {
       Booking booking = repository.findById(id)
               .orElseThrow(() -> new NotFoundException(String.format("Бронирования с ид %s не найдено", id)));
       if (userId == null) {
           throw new NotFoundException(String.format("Не задан владелец"));
       }
       if (booking.getItem().getOwner().getId() != userId) {
           throw new NotFoundException(String.format("Пользователь не владелец вещи"));
       }
       if (approved == null) {
           throw new ValidationException(String.format("Не задано действие"));
       }
       if (approved.booleanValue() && booking.getStatus().equals(BookingStatus.APPROVED)) {
           throw new ValidationException(String.format("Запрос уже подтвержден"));
       }
       if (!approved.booleanValue() && booking.getStatus().equals(BookingStatus.REJECTED)) {
           throw new ValidationException(String.format("Запрос уже подтвержден"));
       }
       if (approved.booleanValue()) {
           booking.setStatus(BookingStatus.APPROVED);
       } else {
           booking.setStatus(BookingStatus.REJECTED);
       }
       return toBookingDtoResponse(repository.save(booking));
    }

    public BookingDtoResponse getBookingById(Long id, Long userId) {
        Booking booking = repository.findByIdAndBookerOwner(id, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ид %s не найдена", id)));
        return toBookingDtoResponse(booking);
    }

    @Transactional
    public List<BookingDtoResponse> getBookings(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        if (state != null) {
            if (!List.of("ALL","CURRENT","PAST","FUTURE", "WAITING", "APPROVED", "REJECTED").contains(state)) {
                throw new InvalidDataException(String.format("Unknown state: " + state));
            }
        }
        return repository.findByBookerAndState(userId, state).stream()
                .map(e -> toBookingDtoResponse(e))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BookingDtoResponse> getBookingsOwner(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        if (state != null) {
            if (!List.of("ALL","CURRENT","PAST","FUTURE", "WAITING", "APPROVED", "REJECTED").contains(state)) {
                throw new InvalidDataException(String.format("Unknown state: " + state));
            }
        }
        return repository.findByBookerAndStateOwner(userId, state).stream()
                .map(e -> toBookingDtoResponse(e))
                .collect(Collectors.toList());
    }
}
