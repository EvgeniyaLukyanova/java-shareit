package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class RequestDtoJsonTest {
    @Autowired
    private JacksonTester<RequestDtoResponse> json;
    @Autowired
    private JacksonTester<RequestDto> jsonD;

    @Test
    void testSerializationRequestDto() throws Exception {
        UserDto user = new UserDto(1L, "user@user.com", "user");

        ItemDto item = new ItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true, user, 1L);

        RequestDtoResponse requestDtoResponse = new RequestDtoResponse(
                1L,
                "Хотел бы воспользоваться щёткой для обуви",
                LocalDateTime.of(2023,6,16,10,0,0),
                List.of(item));

        JsonContent<RequestDtoResponse> result = json.write(requestDtoResponse);

        assertThat(result).hasJsonPathValue("$.id", requestDtoResponse.getId());
        assertThat(result).hasJsonPathValue("$.description", requestDtoResponse.getDescription());
        assertThat(result).hasJsonPathValue("$.created", requestDtoResponse.getCreated());
        assertThat(result).hasJsonPathValue("$.items[0].id", item.getId());
        assertThat(result).hasJsonPathValue("$.items[0].name", item.getName());
        assertThat(result).hasJsonPathValue("$.items[0].owner.id", user.getId());
        assertThat(result).hasJsonPathValue("$.items[0].available", item.getAvailable());
    }

    @Test
    void testDeserializationRequestDto() throws Exception {
        String json = "{\n" +
                "\"id\":1,\n" +
                "\"description\":\"Хотел бы воспользоваться щёткой для обуви\",\n" +
                "\"requestor\":{\n" +
                "               \"id\":1,\n" +
                "               \"email\":\"user@user.com\",\n" +
                "               \"name\":\"user\"\n" +
                "               },\n" +
                "\"created\":\"2023-06-16T10:00:00\"\n" +
                "}";

        UserDto user = new UserDto(1L, "user@user.com", "user");

        RequestDto requestDto = new RequestDto(
                1L,
                "Хотел бы воспользоваться щёткой для обуви",
                user,
                LocalDateTime.of(2023,6,16,10,0,0)
                );

        ObjectContent<RequestDto> result = jsonD.parse(json);

        assertThat(result).usingRecursiveComparison().isEqualTo(requestDto);
    }
}