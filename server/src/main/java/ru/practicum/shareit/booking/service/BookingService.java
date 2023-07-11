package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse createBooking(BookingDto bookingDto, Long userId);

    BookingDtoResponse requestApprovReject(Long id, Long userId, Boolean approved);

    BookingDtoResponse getBookingById(Long id, Long userId);

    List<BookingDtoResponse> getBookings(Long userId, String state, Integer from, Integer size);

    List<BookingDtoResponse> getBookingsOwner(Long userId, String state, Integer from, Integer size);
}
