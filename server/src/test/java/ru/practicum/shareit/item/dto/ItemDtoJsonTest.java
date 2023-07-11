package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.reference.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDtoResponse> json;
    @Autowired
    private JacksonTester<ItemDto> jsonD;
    @Autowired
    private JacksonTester<CommentDto> jsonComment;
    @Autowired
    private JacksonTester<CommentDto> jsonCommentD;

    @Test
    void testSerializationItemDto() throws Exception {
        UserDto user = new UserDto(1L, "user@user.com", "user");

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

        ItemDto item = new ItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true, user, 1L);
        CommentDto comment = new CommentDto(1L,
                "comment",
                item,
                "commenter",
                LocalDateTime.of(2023,6,18,10,0,0)
        );

        ItemDtoResponse itemDtoResponse = new ItemDtoResponse(1L,
                "Аккумуляторная дрель",
                "Аккумуляторная дрель + аккумулятор",
                true,
                user,
                lastBookingDto,
                nextBookingDto,
                List.of(comment),
                1L
                );

        JsonContent<ItemDtoResponse> result = json.write(itemDtoResponse);

        assertThat(result).hasJsonPathValue("$.id", itemDtoResponse.getId());
        assertThat(result).hasJsonPathValue("$.name", itemDtoResponse.getName());
        assertThat(result).hasJsonPathValue("$.description", itemDtoResponse.getDescription());
        assertThat(result).hasJsonPathValue("$.available", itemDtoResponse.getAvailable());
        assertThat(result).hasJsonPathValue("$.owner.id", user.getId());
        assertThat(result).hasJsonPathValue("$.lastBooking", lastBookingDto.getId());
        assertThat(result).hasJsonPathValue("$.nextBooking", nextBookingDto.getId());
        assertThat(result).hasJsonPathValue("$.comments[0].id", comment.getId());
        assertThat(result).hasJsonPathValue("$.requestId", 1L);
    }

    @Test
    void testDeserializationItemDto() throws Exception {
        String json = "{\n" +
                "\"id\":1,\n" +
                "\"name\":\"Аккумуляторная дрель\",\n" +
                "\"description\":\"Аккумуляторная дрель + аккумулятор\",\n" +
                "\"available\":true,\n" +
                "\"owner\":{\n" +
                "           \"id\":1,\n" +
                "           \"email\":\"user@user.com\",\n" +
                "           \"name\":\"user\"\n" +
                "},\n" +
                "\"requestId\":1\n" +
                "}";

        UserDto user = new UserDto(1L, "user@user.com", "user");
        ItemDto item = new ItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true, user, 1L);

        ObjectContent<ItemDto> result = jsonD.parse(json);

        assertThat(result).usingRecursiveComparison().isEqualTo(item);
    }

    @Test
    void testSerializationCommentDto() throws Exception {
        UserDto user = new UserDto(1L, "user@user.com", "user");
        ItemDto item = new ItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true, user, 1L);
        CommentDto comment = new CommentDto(1L,
                "comment",
                item,
                "commenter",
                LocalDateTime.of(2023,6,18,10,0,0)
        );

        JsonContent<CommentDto> result = jsonComment.write(comment);

        assertThat(result).hasJsonPathValue("$.id", comment.getId());
        assertThat(result).hasJsonPathValue("$.text", comment.getText());
        assertThat(result).hasJsonPathValue("$.item.id", item.getId());
        assertThat(result).hasJsonPathValue("$.item.name", item.getName());
        assertThat(result).hasJsonPathValue("$.item.owner.id", user.getId());
        assertThat(result).hasJsonPathValue("$.item.available", item.getAvailable());
        assertThat(result).hasJsonPathValue("$.authorName", comment.getAuthorName());
        assertThat(result).hasJsonPathValue("$.created", comment.getCreated());
    }

    @Test
    void testDeserializationCommentDto() throws Exception {
        String json = "{\n" +
                "\"id\":1,\n" +
                "\"text\":\"comment\",\n" +
                "\"item\":{\n" +
                "          \"id\":1,\n" +
                "          \"name\":\"Аккумуляторная дрель\",\n" +
                "          \"description\":\"Аккумуляторная дрель + аккумулятор\",\n" +
                "          \"available\":true,\n" +
                "          \"owner\":{\n" +
                "                     \"id\":1,\n" +
                "                     \"email\":\"user@user.com\",\n" +
                "                     \"name\":\"user\"\n" +
                "                     },\n" +
                "          \"requestId\":1\n" +
                "          },\n" +
                "\"authorName\":\"commenter\",\n" +
                "\"created\":\"2023-06-18T10:00:00\"\n" +
                "}";
        UserDto user = new UserDto(1L, "user@user.com", "user");
        ItemDto item = new ItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true, user, 1L);
        CommentDto comment = new CommentDto(1L,
                "comment",
                item,
                "commenter",
                LocalDateTime.of(2023,6,18,10,0,0)
        );

        ObjectContent<CommentDto> result = jsonCommentD.parse(json);

        assertThat(result).usingRecursiveComparison().isEqualTo(comment);
    }
}