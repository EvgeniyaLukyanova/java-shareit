package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private int id;
    @NotBlank//(groups = {Create.class})
    private String name;
    @NotBlank//(groups = {Create.class})
    private String description;
    @NotNull//(groups = {Create.class})
    private Boolean available;
    private User owner;
}
