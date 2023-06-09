package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.shareit.constants.Constants.dateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private Long id;
    @NotBlank
    @Size(max = 1000)
    private String description;
    private UserDto requestor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateFormat)
    private LocalDateTime created;
}
