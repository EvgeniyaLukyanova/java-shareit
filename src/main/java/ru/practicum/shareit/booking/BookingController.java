package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InvalidDataException;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoResponse create(@Valid @RequestBody BookingDto booking, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Начало бронирования: {}", booking);
        BookingDtoResponse bookingDto = bookingService.createBooking(booking, userId);
        log.info("Бронирование окончено: {}", bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoResponse patch(@PathVariable("bookingId") Long id, @RequestParam("approved") Boolean approved, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Начало подтверждение/отклонение запроса на бронирование");
        BookingDtoResponse bookingDto = bookingService.requestApprovReject(id, userId, approved);
        log.info("Бронирование подтверждено/отклонено");
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@PathVariable("bookingId") Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение информации о бронировании с ид {}", id);
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping
    public Collection<BookingDtoResponse> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(required = false) String state) {
        log.info("Получение списка бронирований со статусом {}", state);
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoResponse> getBookingsOwner(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(required = false) String state) {
        log.info("Получение списка бронирований со статусом {}", state);
        return bookingService.getBookingsOwner(userId, state);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidData(final InvalidDataException e) {
        return Map.of("error", e.getMessage());
    }
}
