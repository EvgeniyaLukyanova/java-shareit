package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.valid.StartBeforeEndDateValid;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookItemRequestDto {
	private long itemId;
	@FutureOrPresent
	@NotNull
	private LocalDateTime start;
	@Future
	@NotNull
	private LocalDateTime end;
}
