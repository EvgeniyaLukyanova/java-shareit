package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.constants.Constants.dateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDtoResponse {
    private Long id;
    @NotBlank
    @Size(max = 1000)
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateFormat)
    private LocalDateTime created;
    private List<ItemDto> items;
}
