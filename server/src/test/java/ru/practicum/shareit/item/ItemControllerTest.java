package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.reference.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
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
import static ru.practicum.shareit.constants.Constants.dateFormat;
import static ru.practicum.shareit.constants.Constants.requestHeaderForUser;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    ItemService itemService;
    @InjectMocks
    ItemController controller;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;
    private UserDto userDto;
    private ItemDto itemDto;

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
    }

    @Test
    void createItem() throws Exception {

        when(itemService.createItem(itemDto, userDto.getId())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header(requestHeaderForUser, userDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.owner.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.owner.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.owner.name", is(userDto.getName())));
    }

    @Test
    void patchItem() throws Exception {
        ItemDto updateItemDto = new ItemDto();
        updateItemDto.setId(1L);
        updateItemDto.setAvailable(false);

        ItemDto newItemDto = itemDto;
        newItemDto.setAvailable(false);

        when(itemService.partialUpdate(any(), any(), any())).thenReturn(newItemDto);

        mvc.perform(patch("/items/1")
                        .header(requestHeaderForUser, userDto.getId())
                        .content(mapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(newItemDto.getName())))
                .andExpect(jsonPath("$.description", is(newItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(newItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(newItemDto.getRequestId())))
                .andExpect(jsonPath("$.owner.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.owner.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.owner.name", is(userDto.getName())));
    }

    private ItemDtoResponse getItemDtoResponse() {
        UserDto commenter = new UserDto(2L, "commenter@user.com", "commenter");

        BookingDto lastBookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2023,6,16,10,0,0),
                LocalDateTime.of(2023,6,18,10,0,0),
                1L,
                2L,
                BookingStatus.APPROVED);
        BookingDto nextBookingDto = new BookingDto(
                2L,
                LocalDateTime.of(2023,6,19,10,0,0),
                LocalDateTime.of(2023,6,20,10,0,0),
                1L,
                3L,
                BookingStatus.APPROVED);

        CommentDto comment = new CommentDto(1L,
                "comment",
                itemDto,
                "commenter",
                LocalDateTime.of(2023,6,18,10,0,0)
        );
        ItemDtoResponse itemDtoResponse = new ItemDtoResponse(1L,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                userDto,
                lastBookingDto,
                nextBookingDto,
                List.of(comment),
                null
        );
        return itemDtoResponse;
    }

    @Test
    void getItemById() throws Exception {
        ItemDtoResponse itemDtoResponse = getItemDtoResponse();
        when(itemService.getItemById(any(), any())).thenReturn(itemDtoResponse);

        mvc.perform(get("/items/1")
                        .header(requestHeaderForUser, userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDtoResponse.getRequestId())))
                .andExpect(jsonPath("$.owner.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.owner.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.owner.name", is(userDto.getName())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDtoResponse.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDtoResponse.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].id", is(itemDtoResponse.getComments().get(0).getId()), Long.class));
    }

    @Test
    void findAll() throws Exception {
        ItemDtoResponse itemDtoResponse = getItemDtoResponse();
        when(itemService.getItems(any(), any(), any())).thenReturn(List.of(itemDtoResponse));

        mvc.perform(get("/items", 0, 1)
                        .header(requestHeaderForUser, userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDtoResponse.getRequestId())))
                .andExpect(jsonPath("$[0].owner.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].owner.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[0].owner.name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemDtoResponse.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemDtoResponse.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].id", is(itemDtoResponse.getComments().get(0).getId()), Long.class));
    }

    @Test
    void findAvailableItem() throws Exception {
        when(itemService.getAvailableItems(any(), any(), any())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "дРелЬ")
                        .param("from", "0")
                        .param("size", "1")
                        .header(requestHeaderForUser, userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$[0].owner.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].owner.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[0].owner.name", is(userDto.getName())));
    }

    @Test
    void createComment() throws Exception {
        UserDto commenter = new UserDto(2L, "commenter@user.com", "commenter");

        CommentDto commentDto = new CommentDto(1L,
                "comment",
                itemDto,
                "commenter",
                LocalDateTime.of(2023,6,18,10,0,0)
        );

        when(itemService.createComment(any(), any(), any())).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header(requestHeaderForUser, userDto.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.item.id", is(commentDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(DateTimeFormatter.ofPattern(dateFormat)))));
    }
}