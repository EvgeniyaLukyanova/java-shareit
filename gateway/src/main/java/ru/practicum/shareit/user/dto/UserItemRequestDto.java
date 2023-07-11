package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserItemRequestDto {
    private Long id;
    @Email
    @Size(max = 512)
    @NotBlank
    private String email;
    @NotBlank
    @Size(max = 255)
    private String name;
}
