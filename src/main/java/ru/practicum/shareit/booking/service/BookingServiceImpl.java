package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.reference.BookingStatus;
import ru.practicum.shareit.booking.reference.BookingStatusParameter;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository repository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public BookingDtoResponse createBooking(BookingDto bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ид %s не найдена", bookingDto.getItemId())));
        if (item.getOwner().getId().equals(user.getId())) {
            throw new NotFoundException(String.format("Вещь с ид %s не найдена", bookingDto.getItemId()));
        }
        if (!item.getAvailable().booleanValue()) {
            throw new ValidationException(String.format("Вещь с ид %s не доступна для бронирования", bookingDto.getItemId()));
        }
        if (repository.findByStartDateBeforeEndDateAfter(bookingDto.getItemId(), bookingDto.getStart(), bookingDto.getEnd()).size() > 0) {
            throw new ValidationException(String.format("Вещь с ид %s уже забронирована", bookingDto.getItemId()));
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDtoResponse(repository.save(BookingMapper.toBooking(bookingDto, item, user)));
    }

    @Transactional
    @Override
    public BookingDtoResponse requestApprovReject(Long id, Long userId, Boolean approved) {
       Booking booking = repository.findById(id)
               .orElseThrow(() -> new NotFoundException(String.format("Бронирования с ид %s не найдено", id)));
       if (userId == null) {
           throw new NotFoundException(String.format("Не задан владелец"));
       }
       if (!booking.getItem().getOwner().getId().equals(userId)) {
           throw new NotFoundException(String.format("Пользователь не владелец вещи"));
       }
       if (approved == null) {
           throw new ValidationException(String.format("Не задано действие"));
       }
       if (approved.booleanValue() && booking.getStatus().equals(BookingStatus.APPROVED)) {
           throw new ValidationException(String.format("Запрос уже подтвержден"));
       }
       if (!approved.booleanValue() && booking.getStatus().equals(BookingStatus.REJECTED)) {
           throw new ValidationException(String.format("Запрос уже отклонен"));
       }
       if (approved.booleanValue()) {
           booking.setStatus(BookingStatus.APPROVED);
       } else {
           booking.setStatus(BookingStatus.REJECTED);
       }
       return BookingMapper.toBookingDtoResponse(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoResponse getBookingById(Long id, Long userId) {
        Booking booking = repository.findByIdAndBookerOwner(id, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ид %s не найдена", id)));
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getBookings(Long userId, String state, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        if (state != null) {
            HashSet<String> statusValues = new HashSet<String>();
            for (BookingStatusParameter status : BookingStatusParameter.values()) {
                statusValues.add(status.name());
            }
            if (!statusValues.contains(state)) {
                throw new InvalidDataException(String.format("Unknown state: " + state));
            }
        }
        Long pageNo = null;
        if (from != null & size != null) {
            pageNo = Math.round(Math.ceil((from + 1) * 1.0 / size));
            return repository.findByBookerAndState(userId, state, size, pageNo).stream()
                    .map(e -> BookingMapper.toBookingDtoResponse(e))
                    .collect(Collectors.toList());
        } else {
            return repository.findByBookerAndState(userId, state).stream()
                    .map(e -> BookingMapper.toBookingDtoResponse(e))
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoResponse> getBookingsOwner(Long userId, String state, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        if (state != null) {
            HashSet<String> statusValues = new HashSet<String>();
            for (BookingStatusParameter status : BookingStatusParameter.values()) {
                statusValues.add(status.name());
            }
            if (!statusValues.contains(state)) {
                throw new InvalidDataException(String.format("Unknown state: " + state));
            }
        }
        Long pageNo = null;
        if (from != null & size != null) {
            pageNo = Math.round(Math.ceil((from + 1) * 1.0 / size));
            return repository.findByBookerAndStateOwner(userId, state, size, pageNo).stream()
                    .map(e -> BookingMapper.toBookingDtoResponse(e))
                    .collect(Collectors.toList());
        } else {
            return repository.findByBookerAndStateOwner(userId, state).stream()
                    .map(e -> BookingMapper.toBookingDtoResponse(e))
                    .collect(Collectors.toList());
        }
    }
}
