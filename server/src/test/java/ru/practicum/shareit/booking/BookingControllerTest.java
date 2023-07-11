package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.reference.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Constants.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    BookingService bookingService;
    @InjectMocks
    BookingController controller;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;
    private UserDto userDto;
    private ItemDto itemDto;
    private UserDto booker;
    private BookingDtoResponse bookingDtoResponse;


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDto = new UserDto(
                1L,
                "user@user.com",
                "user");
        itemDto = new ItemDto(1L,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                userDto,
                null);
        UserDto booker = new UserDto(2L, "booker@user.com", "booker");
        bookingDtoResponse = new BookingDtoResponse(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                itemDto,
                booker,
                BookingStatus.APPROVED);
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any(), any())).thenReturn(bookingDtoResponse);

        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L,
                1L,
                BookingStatus.APPROVED);

        mvc.perform(post("/bookings")
                        .header(REQUEST_HEADER_FOR_USER, userDto.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoResponse.getStart().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))))
                .andExpect(jsonPath("$.end", is(bookingDtoResponse.getEnd().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))))
                .andExpect(jsonPath("$.item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().toString())));
    }

    @Test
    void patchBooking() throws Exception {
        bookingDtoResponse.setStatus(BookingStatus.REJECTED);
        when(bookingService.requestApprovReject(any(), any(), any())).thenReturn(bookingDtoResponse);

        mvc.perform(patch("/bookings/1")
                        .header(REQUEST_HEADER_FOR_USER, userDto.getId())
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoResponse.getStart().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))))
                .andExpect(jsonPath("$.end", is(bookingDtoResponse.getEnd().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))))
                .andExpect(jsonPath("$.item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().toString())));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(any(), any())).thenReturn(bookingDtoResponse);

        mvc.perform(get("/bookings/1")
                        .header(REQUEST_HEADER_FOR_USER, userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoResponse.getStart().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))))
                .andExpect(jsonPath("$.end", is(bookingDtoResponse.getEnd().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))))
                .andExpect(jsonPath("$.item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().toString())));
    }

    @Test
    void getBookings() throws Exception {
        when(bookingService.getBookings(any(), any(), any(), any())).thenReturn(List.of(bookingDtoResponse));

        mvc.perform(get("/bookings")
                        .header(REQUEST_HEADER_FOR_USER, userDto.getId())
                        .param("state", "APPROVED")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtoResponse.getStart().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))))
                .andExpect(jsonPath("$[0].end", is(bookingDtoResponse.getEnd().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))))
                .andExpect(jsonPath("$[0].item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoResponse.getStatus().toString())));
    }

    @Test
    void getBookingsOwner() throws Exception {
        when(bookingService.getBookingsOwner(any(), any(), any(), any())).thenReturn(List.of(bookingDtoResponse));

        mvc.perform(get("/bookings/owner")
                        .header(REQUEST_HEADER_FOR_USER, userDto.getId())
                        .param("state", "APPROVED")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtoResponse.getStart().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))))
                .andExpect(jsonPath("$[0].end", is(bookingDtoResponse.getEnd().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))))
                .andExpect(jsonPath("$[0].item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoResponse.getStatus().toString())));
    }

    @Test
    void getBookingsInvalidDataException() throws Exception {
        when(bookingService.getBookings(any(), any(), any(), any())).thenThrow(new InvalidDataException(String.format("Unknown state: APPROV")));

        mvc.perform(get("/bookings", 0, 1)
                        .header(REQUEST_HEADER_FOR_USER, userDto.getId())
                        .param("state", "APPROV")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isBadRequest());
    }

}