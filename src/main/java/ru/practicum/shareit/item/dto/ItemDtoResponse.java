package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoResponse {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private UserDto owner;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
