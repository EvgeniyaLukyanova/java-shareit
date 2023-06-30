package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InvalidDataException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.Map;

import static ru.practicum.shareit.constants.Constants.requestHeaderForUser;

@RestController
@RequestMapping("/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoResponse create(@Valid @RequestBody BookingDto booking,
                                     @RequestHeader(requestHeaderForUser) Long userId) {
        log.info("Начало бронирования: {}", booking);
        BookingDtoResponse bookingDto = bookingService.createBooking(booking, userId);
        log.info("Бронирование окончено: {}", bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoResponse patch(@PathVariable("bookingId") Long id,
                                    @RequestParam("approved") Boolean approved,
                                    @RequestHeader(requestHeaderForUser) Long userId) {
        log.info("Начало подтверждение/отклонение запроса на бронирование");
        BookingDtoResponse bookingDto = bookingService.requestApprovReject(id, userId, approved);
        log.info("Бронирование подтверждено/отклонено");
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDtoResponse getBookingById(@PathVariable("bookingId") Long id,
                                             @RequestHeader(requestHeaderForUser) Long userId) {
        log.info("Получение информации о бронировании с ид {}", id);
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingDtoResponse> getBookings(@RequestHeader(requestHeaderForUser) Long userId,
                                                      @RequestParam(required = false) String state,
                                                      @RequestParam(required = false) @Min(0) Integer from,
                                                      @RequestParam(required = false) @Min(1) Integer size) {
        log.info("Получение списка бронирований со статусом {}", state);
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingDtoResponse> getBookingsOwner(@RequestHeader(requestHeaderForUser) Long userId,
                                                           @RequestParam(required = false) String state,
                                                           @RequestParam(required = false) @Min(0) Integer from,
                                                           @RequestParam(required = false) @Min(1) Integer size) {
        log.info("Получение списка бронирований со статусом {}", state);
        return bookingService.getBookingsOwner(userId, state, from, size);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidData(final InvalidDataException e) {
        return Map.of("error", e.getMessage());
    }
}
