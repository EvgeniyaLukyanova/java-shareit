package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank
    @Size(max = 255)
    private String name;
    @NotBlank
    @Size(max = 1000)
    private String description;
    @NotNull
    private Boolean available;
    private UserDto owner;
    private Long requestId;
}
