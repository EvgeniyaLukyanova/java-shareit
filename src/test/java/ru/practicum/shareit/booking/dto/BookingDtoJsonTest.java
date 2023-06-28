package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.booking.reference.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDtoResponse> json;
    @Autowired
    private JacksonTester<BookingDto> jsonD;

    @Test
    void testSerializationBookingDto() throws Exception {
        UserDto user = new UserDto(1L, "user@user.com", "user");

        ItemDto item = new ItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true, user, 1L);

        UserDto booker = new UserDto(2L, "booker@user.com", "booker");

        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse(
                1L,
                LocalDateTime.of(2023,6,16,10,0,0),
                LocalDateTime.of(2023,6,18,10,0,0),
                item,
                booker,
                BookingStatus.APPROVED);

        JsonContent<BookingDtoResponse> result = json.write(bookingDtoResponse);

        assertThat(result).hasJsonPathValue("$.id", bookingDtoResponse.getId());
        assertThat(result).hasJsonPathValue("$.start", bookingDtoResponse.getStart());
        assertThat(result).hasJsonPathValue("$.end", bookingDtoResponse.getEnd());
        assertThat(result).hasJsonPathValue("$.item.id", item.getId());
        assertThat(result).hasJsonPathValue("$.item.name", item.getName());
        assertThat(result).hasJsonPathValue("$.item.owner.id", user.getId());
        assertThat(result).hasJsonPathValue("$.item.available", item.getAvailable());
        assertThat(result).hasJsonPathValue("$.booker.id", booker.getId());
        assertThat(result).hasJsonPathValue("$.booker.name", booker.getName());
        assertThat(result).hasJsonPathValue("$.booker.email", booker.getEmail());
        assertThat(result).hasJsonPathValue("$.status", bookingDtoResponse.getStatus());
    }

    @Test
    void testDeserializationBookingDto() throws Exception {
        String json = "{\n" +
                "\"id\":1,\n" +
                "\"start\":\"2023-06-16T10:00:00\",\n" +
                "\"end\":\"2023-06-18T10:00:00\",\n" +
                "\"itemId\":1,\n" +
                "\"bookerId\":1,\n" +
                "\"status\":\"APPROVED\"\n" +
                "}";

        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2023,6,16,10,0,0),
                LocalDateTime.of(2023,6,18,10,0,0),
                1L,
                1L,
                BookingStatus.APPROVED);

        ObjectContent<BookingDto> result = jsonD.parse(json);

        assertThat(result).usingRecursiveComparison().isEqualTo(bookingDto);
    }
}