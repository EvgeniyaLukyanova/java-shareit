package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InvalidDataException;
import java.util.Collection;
import java.util.Map;

import static ru.practicum.shareit.constants.Constants.REQUEST_HEADER_FOR_USER;

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
    public BookingDtoResponse createBooking(@RequestBody BookingDto booking,
                                            @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Начало бронирования: {}", booking);
        BookingDtoResponse bookingDto = bookingService.createBooking(booking, userId);
        log.info("Бронирование окончено: {}", bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoResponse requestApprovReject(@PathVariable("bookingId") Long id,
                                                  @RequestParam("approved") Boolean approved,
                                                  @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Начало подтверждение/отклонение запроса на бронирование");
        BookingDtoResponse bookingDto = bookingService.requestApprovReject(id, userId, approved);
        log.info("Бронирование подтверждено/отклонено");
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoResponse getBookingById(@PathVariable("bookingId") Long id,
                                             @RequestHeader(REQUEST_HEADER_FOR_USER) Long userId) {
        log.info("Получение информации о бронировании с ид {}", id);
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingDtoResponse> getBookings(@RequestHeader(REQUEST_HEADER_FOR_USER) Long userId,
                                                      @RequestParam(required = false) String state,
                                                      @RequestParam(required = false) Integer from,
                                                      @RequestParam(required = false) Integer size) {
        log.info("Получение списка бронирований со статусом {}", state);
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingDtoResponse> getBookingsOwner(@RequestHeader(REQUEST_HEADER_FOR_USER) Long userId,
                                                           @RequestParam(required = false) String state,
                                                           @RequestParam(required = false) Integer from,
                                                           @RequestParam(required = false) Integer size) {
        log.info("Получение списка бронирований со статусом {}", state);
        return bookingService.getBookingsOwner(userId, state, from, size);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidData(final InvalidDataException e) {
        return Map.of("error", e.getMessage());
    }
}
