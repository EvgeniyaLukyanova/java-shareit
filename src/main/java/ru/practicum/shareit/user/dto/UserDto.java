package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int id;
    @Email//(groups = {Create.class, Update.class})
    @NotBlank//(groups = {Create.class})
    private String email;
    @NotBlank//(groups = {Create.class})
    private String name;
}
