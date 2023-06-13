package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto user) {
        log.info("Начинаем добавлять пользователя: {}", user);
        UserDto userDto = userService.createUser(user);
        log.info("Пользователь добавлен: {}", user);
        return userDto;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto patch(@RequestBody UserDto user, @PathVariable Long id) {
        log.info("Начинаем изменять пользователя: {}", user);
        UserDto userDto = userService.partialUpdate(user, id);
        log.info("Пользователь изменен: {}", user);
        return userDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> findAll() {
        log.info("Получение списка всех пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Получение пользователя с ид {}", id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        log.info("Удаляем пользователя с ид {}", id);
        userService.delete(id);
        log.info("Пользователь с ид {} удален", id);
    }
}
