package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStartDate());
        bookingDto.setEnd(booking.getEndDate());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setBookerId(booking.getBooker().getId());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        BookingDtoResponse bookingDto = new BookingDtoResponse();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStartDate());
        bookingDto.setEnd(booking.getEndDate());
        bookingDto.setItem(toItemDto(booking.getItem()));
        bookingDto.setBooker(toUserDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        if (bookingDto != null) {
            Booking booking = new Booking();
            booking.setId(bookingDto.getId());
            booking.setStartDate(bookingDto.getStart());
            booking.setEndDate(bookingDto.getEnd());
            booking.setItem(item);
            booking.setBooker(user);
            booking.setStatus(bookingDto.getStatus());
            return booking;
        } else {
            return null;
        }
    }

}
