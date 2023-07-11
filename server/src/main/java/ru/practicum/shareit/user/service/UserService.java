package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto userDto, Long id);

    UserDto getUserById(long id);

    List<UserDto> getUsers();

    void delete(Long id);
}
