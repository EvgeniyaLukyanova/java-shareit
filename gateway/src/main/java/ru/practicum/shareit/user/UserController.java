package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserItemRequestDto;
import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получение всех пользователей");
        return userClient.getUsers();
    }

    @PostMapping
    public ResponseEntity<Object> userItem(@RequestBody @Valid UserItemRequestDto user) {
        log.info("Создаем пользователя: {}", user);
        return userClient.userItem(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> userPartItem(@RequestBody UserItemRequestDto user, @PathVariable Long id) {
        log.info("Изменяем пользователя: {}", user);
        return userClient.userPatchItem(id, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Получение пользователя с ид {}", id);
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Удаление пользователя с ид {}", id);
        return userClient.delete(id);
    }
}
