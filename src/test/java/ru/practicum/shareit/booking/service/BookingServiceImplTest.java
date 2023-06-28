package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.reference.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    BookingRepository repository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    private BookingService bookingService;
    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(repository,
                userRepository,
                itemRepository);

        user = new User(
                1L,
                "user@user.com",
                "user");

        item = new Item();
        item.setId(1L);
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);

        User booker = new User(2L, "booker@user.com", "booker");
        booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.of(2023,6,16,10,0,0));
        booking.setEndDate(LocalDateTime.of(2023,6,17,10,0,0));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void createBooking() {
        Long userId = 1L;
        Long itemId = 1L;

        User owner = new User(2L, "owner@user.com", "owner");
        item.setOwner(owner);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2023,6,16,10,0,0));
        bookingDto.setEnd(LocalDateTime.of(2023,6,17,10,0,0));
        bookingDto.setItemId(itemId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(repository.findByStartDateBeforeEndDateAfter(itemId, bookingDto.getStart(), bookingDto.getEnd())).thenReturn(List.of());

        Booking bookingNew = BookingMapper.toBooking(bookingDto, item, user);
        bookingNew.setStatus(BookingStatus.WAITING);
        when(repository.save(bookingNew)).thenReturn(bookingNew);

        BookingDtoResponse resultBookingDto = bookingService.createBooking(bookingDto, userId);

        assertEquals(bookingDto.getStart(), resultBookingDto.getStart());
        assertEquals(bookingDto.getEnd(), resultBookingDto.getEnd());
        assertEquals(bookingDto.getItemId(), resultBookingDto.getItem().getId());
        verify(repository).save(bookingNew);
    }

    @Test
    void createBooking_existBooking() {
        Long userId = 1L;
        Long itemId = 1L;

        User owner = new User(2L, "owner@user.com", "owner");
        item.setOwner(owner);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2023,6,16,10,0,0));
        bookingDto.setEnd(LocalDateTime.of(2023,6,17,10,0,0));
        bookingDto.setItemId(itemId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(repository.findByStartDateBeforeEndDateAfter(itemId, bookingDto.getStart(), bookingDto.getEnd())).thenReturn(List.of(new Booking()));

        Throwable exception = assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, userId));
        assertEquals("Вещь с ид 1 уже забронирована", exception.getMessage());
        verify(repository,never()).save(any());
    }

    @Test
    void createBooking_availableFalse() {
        Long userId = 1L;
        Long itemId = 1L;

        User owner = new User(2L, "owner@user.com", "owner");
        item.setAvailable(false);
        item.setOwner(owner);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2023,6,16,10,0,0));
        bookingDto.setEnd(LocalDateTime.of(2023,6,17,10,0,0));
        bookingDto.setItemId(itemId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Throwable exception = assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, userId));
        assertEquals("Вещь с ид 1 не доступна для бронирования", exception.getMessage());
        verify(repository,never()).save(any());
    }

    @Test
    void createBooking_ownerItem() {
        Long userId = 1L;
        Long itemId = 1L;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2023,6,16,10,0,0));
        bookingDto.setEnd(LocalDateTime.of(2023,6,17,10,0,0));
        bookingDto.setItemId(itemId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Throwable exception = assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, userId));
        assertEquals("Вещь с ид 1 не найдена", exception.getMessage());
        verify(repository,never()).save(any());
    }

    @Test
    void requestApprovReject() {
        Long userId = 1L;
        Long bookingId = 1L;
        Item item = new Item();
        item.setId(1L);
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setAvailable(true);
        item.setOwner(user);

        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoResponse resultBookingDto = bookingService.requestApprovReject(bookingId, userId, true);
        assertEquals(booking.getStartDate(), resultBookingDto.getStart());
        assertEquals(booking.getEndDate(), resultBookingDto.getEnd());
        assertEquals(ItemMapper.toItemDto(booking.getItem()), resultBookingDto.getItem());
        assertEquals(BookingStatus.APPROVED, resultBookingDto.getStatus());
    }

    @Test
    void requestApprovReject_noOwner() {
        Long userId = 1L;
        Long bookingId = 1L;

        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        Throwable exception = assertThrows(NotFoundException.class, () -> bookingService.requestApprovReject(bookingId, 3L, true));
        assertEquals("Пользователь не владелец вещи", exception.getMessage());
    }

    @Test
    void requestApprovReject_approvedEmpty() {
        Long userId = 1L;
        Long bookingId = 1L;

        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        Throwable exception = assertThrows(ValidationException.class, () -> bookingService.requestApprovReject(bookingId, userId, null));
        assertEquals("Не задано действие", exception.getMessage());
    }

    @Test
    void requestApprovReject_bookingApproved() {
        Long userId = 1L;
        Long bookingId = 1L;
        booking.setStatus(BookingStatus.APPROVED);

        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        Throwable exception = assertThrows(ValidationException.class, () -> bookingService.requestApprovReject(bookingId, userId, true));
        assertEquals("Запрос уже подтвержден", exception.getMessage());
    }

    @Test
    void requestApprovReject_bookingRejected() {
        Long userId = 1L;
        Long bookingId = 1L;
        booking.setStatus(BookingStatus.REJECTED);

        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        Throwable exception = assertThrows(ValidationException.class, () -> bookingService.requestApprovReject(bookingId, userId, false));
        assertEquals("Запрос уже отклонен", exception.getMessage());
    }

    @Test
    void requestApprovReject_reject() {
        Long userId = 1L;
        Long bookingId = 1L;

        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoResponse resultBookingDto = bookingService.requestApprovReject(bookingId, userId, false);
        assertEquals(booking.getStartDate(), resultBookingDto.getStart());
        assertEquals(booking.getEndDate(), resultBookingDto.getEnd());
        assertEquals(ItemMapper.toItemDto(booking.getItem()), resultBookingDto.getItem());
        assertEquals(BookingStatus.REJECTED, resultBookingDto.getStatus());
    }

    @Test
    void requestApprovReject_userEmpty() {
        Long bookingId = 1L;

        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        Throwable exception = assertThrows(NotFoundException.class, () -> bookingService.requestApprovReject(bookingId, null, true));
        assertEquals("Не задан владелец", exception.getMessage());
    }

    @Test
    void getBookings() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = null;
        Integer size = null;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(repository.findByBookerAndState(userId, state, size, null)).thenReturn(List.of(booking));

        List<BookingDtoResponse> resultBookingDto = bookingService.getBookings(userId, state, from, size);
        assertEquals(1, resultBookingDto.size());
        assertEquals(1, resultBookingDto.get(0).getId());
    }

    @Test
    void getBookings_errorState() {
        Long userId = 1L;
        String state = "FFF";
        Integer from = null;
        Integer size = null;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Throwable exception = assertThrows(InvalidDataException.class, () -> bookingService.getBookings(userId, state, from, size));
        assertEquals("Unknown state: FFF", exception.getMessage());
    }

    @Test
    void getBookings_fromSizeNotEmpty() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(repository.findByBookerAndState(userId, state, size, 1L)).thenReturn(List.of(booking));

        List<BookingDtoResponse> resultBookingDto = bookingService.getBookings(userId, state, from, size);
        assertEquals(1, resultBookingDto.size());
        assertEquals(1, resultBookingDto.get(0).getId());
    }

    @Test
    void getBookings_SizeEmpty() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = null;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Throwable exception = assertThrows(ValidationException.class, () -> bookingService.getBookings(userId, state, from, size));
        assertEquals("Должны быть заполненны оба параметра: from, size", exception.getMessage());
    }

    @Test
    void getBookings_fromLessThanZero() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = -1;
        Integer size = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Throwable exception = assertThrows(ValidationException.class, () -> bookingService.getBookings(userId, state, from, size));
        assertEquals("Не верное значение параметра from", exception.getMessage());
    }

    @Test
    void getBookings_fromLessThanOne() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Throwable exception = assertThrows(ValidationException.class, () -> bookingService.getBookings(userId, state, from, size));
        assertEquals("Не верное значение параметра size", exception.getMessage());
    }

    @Test
    void getBookingsOwner() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = null;
        Integer size = null;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(repository.findByBookerAndStateOwner(userId, state, size, null)).thenReturn(List.of(booking));

        List<BookingDtoResponse> resultBookingDto = bookingService.getBookingsOwner(userId, state, from, size);
        assertEquals(1, resultBookingDto.size());
        assertEquals(1, resultBookingDto.get(0).getId());
    }

    @Test
    void getBookingsOwner_errorState() {
        Long userId = 1L;
        String state = "FFF";
        Integer from = null;
        Integer size = null;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Throwable exception = assertThrows(InvalidDataException.class, () -> bookingService.getBookingsOwner(userId, state, from, size));
        assertEquals("Unknown state: FFF", exception.getMessage());
    }

    @Test
    void getBookingsOwner_fromSizeNotEmpty() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(repository.findByBookerAndStateOwner(userId, state, size, 1L)).thenReturn(List.of(booking));

        List<BookingDtoResponse> resultBookingDto = bookingService.getBookingsOwner(userId, state, from, size);
        assertEquals(1, resultBookingDto.size());
        assertEquals(1, resultBookingDto.get(0).getId());
    }

    @Test
    void getBookingsOwner_SizeEmpty() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = null;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Throwable exception = assertThrows(ValidationException.class, () -> bookingService.getBookingsOwner(userId, state, from, size));
        assertEquals("Должны быть заполненны оба параметра: from, size", exception.getMessage());
    }

    @Test
    void getBookingsOwner_fromLessThanZero() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = -1;
        Integer size = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Throwable exception = assertThrows(ValidationException.class, () -> bookingService.getBookingsOwner(userId, state, from, size));
        assertEquals("Не верное значение параметра from", exception.getMessage());
    }

    @Test
    void getBookingsOwner_fromLessThanOne() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Throwable exception = assertThrows(ValidationException.class, () -> bookingService.getBookingsOwner(userId, state, from, size));
        assertEquals("Не верное значение параметра size", exception.getMessage());
    }

    @Test
    void getBookingById() {
        Long userId = 1L;
        Long bookingId = 1L;

        when(repository.findByIdAndBookerOwner(bookingId, userId)).thenReturn(Optional.of(booking));

        BookingDtoResponse resultBookingDto = bookingService.getBookingById(bookingId, userId);
        assertEquals(1, resultBookingDto.getId());
    }
}