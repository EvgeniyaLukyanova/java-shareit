package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Constants.dateFormat;
import static ru.practicum.shareit.constants.Constants.requestHeaderForUser;


@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    @Mock
    RequestService requestService;
    @InjectMocks
    RequestController controller;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;
    private UserDto userDto;
    private ItemDto itemDto;
    private RequestDtoResponse requestDtoResponse;

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
        requestDtoResponse = new RequestDtoResponse(1L,
                "Хотел бы воспользоваться щёткой для обуви",
                LocalDateTime.of(2023,6,18,10,0,0),
                List.of(itemDto));
    }

    @Test
    void createRequest() throws Exception {
        RequestDto requestDto = new RequestDto(1L,
                "Хотел бы воспользоваться щёткой для обуви",
                userDto,
                LocalDateTime.of(2023,6,18,10,0,0));

        when(requestService.createRequest(any(), any())).thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header(requestHeaderForUser, userDto.getId())
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.requestor.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.requestor.name", is(userDto.getName())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().format(DateTimeFormatter.ofPattern(dateFormat)))));
    }

    @Test
    void findRequestsUser() throws Exception {
        when(requestService.getRequests(userDto.getId())).thenReturn(List.of(requestDtoResponse));

        mvc.perform(get("/requests")
                        .header(requestHeaderForUser, userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].created", is(requestDtoResponse.getCreated().format(DateTimeFormatter.ofPattern(dateFormat)))));
    }

    @Test
    void findAllRequests() throws Exception {
        when(requestService.getAllRequests(any(), any(), any())).thenReturn(List.of(requestDtoResponse));

        mvc.perform(get("/requests/all")
                        .header(requestHeaderForUser, userDto.getId())
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].created", is(requestDtoResponse.getCreated().format(DateTimeFormatter.ofPattern(dateFormat)))));
    }

    @Test
    void getItemById() throws Exception {
        when(requestService.getRequestById(any(), any())).thenReturn(requestDtoResponse);

        mvc.perform(get("/requests/1")
                        .header(requestHeaderForUser, userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoResponse.getDescription())))
                .andExpect(jsonPath("$.items[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.created", is(requestDtoResponse.getCreated().format(DateTimeFormatter.ofPattern(dateFormat)))));
    }
}