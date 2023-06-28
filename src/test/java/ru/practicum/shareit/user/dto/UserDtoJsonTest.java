package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;
    @Autowired
    private JacksonTester<UserDto> jsonD;

    @Test
    void testSerializationUserDto() throws Exception {
        UserDto user = new UserDto(1L, "user@user.com", "user");

        JsonContent<UserDto> result = json.write(user);

        assertThat(result).hasJsonPathValue("$.id", user.getId());
        assertThat(result).hasJsonPathValue("$.email", user.getEmail());
        assertThat(result).hasJsonPathValue("$.name", user.getName());
    }

    @Test
    void testDeserializationUserDto() throws Exception {
        String json = "{\n" +
                "\"id\":1,\n" +
                "\"email\":\"user@user.com\",\n" +
                "\"name\":\"user\"\n" +
                "}";

        UserDto user = new UserDto(1L, "user@user.com", "user");

        ObjectContent<UserDto> result = jsonD.parse(json);

        assertThat(result).usingRecursiveComparison().isEqualTo(user);
    }
}