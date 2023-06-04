package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    public void validate(UserDto user, int id) {
        if (userStorage.getUsers().stream()
                .filter(f -> f.getId() != id)
                .map(e -> e.getEmail())
                .collect(Collectors.toList())
                .contains(user.getEmail())) {
            throw new ValidationException("Пользователь с электронной почтой " + user.getEmail() + " уже существует.");
        }
    }

    public void validate(UserDto user) {
        validate(user, user.getId());
    }

    public UserDto createUser(UserDto user) {
        return userMapper.toUserDto(userStorage.createUser(userMapper.toUser(user)));
    }

    public UserDto partialUpdate(UserDto userDto, int id) {
        User user = userStorage.getUserById(id);
        userMapper.updateUser(userDto, user);
        return userMapper.toUserDto(userStorage.updateUser(user));
    }

    public UserDto getUserById(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с ид %s не найден", id));
        }
        return userMapper.toUserDto(user);
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream().map(e -> userMapper.toUserDto(e)).collect(Collectors.toList());
    }

    public void delete(int id) {
        userStorage.deleteUserById(id);
    }
}
